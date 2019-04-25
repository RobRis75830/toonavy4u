package navy.toonavy4u;

import entities.*;
import entities.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import repositories.ComicRepository;
import repositories.SubscriptionRepository;
import repositories.ViewsRepository;

import java.util.List;

import static navy.toonavy4u.LoginController.getEmail;

@Controller
@EnableAutoConfiguration
public class SubscriptionController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private repositories.ViewsRepository ViewsRepository;
    @Autowired
    private ComicRepository comicRepository;

    @RequestMapping(value = "/manageSubscription", method = RequestMethod.POST)
    public ModelAndView manageSubscription(@ModelAttribute("post") Post post, ModelMap model, OAuth2AuthenticationToken authentication) {

        if (authentication != null) {

            String email = getEmail(authentication, authorizedClientService);
            if (subscriptionRepository.findByIdSubscriberAndIdSeries(email, post.getSeries().getId()).isEmpty()) {
                Subscription subscription = new Subscription();
                subscription.setSubscriber(email);
                subscription.setSeries(post.getSeries().getId());
                subscriptionRepository.save(subscription);
                List<Comic> comic= comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(post.getSeries().getId(),1);
                for (int i=0;i<comic.size();i++){
                    if (ViewsRepository.findByIdViewerAndIdComic(email,comic.get(i).getId()).isEmpty()){
                        Views view=new Views();
                        view.setComic(comic.get(i).getId());
                        view.setViewer(email);
                        ViewsRepository.save(view);
                    } }


            } else {
                Subscription subscription = subscriptionRepository.findByIdSubscriberAndIdSeries(email, post.getSeries().getId()).get(0);
                subscriptionRepository.delete(subscription);
            }

            model.addAttribute("seriesId", post.getSeries().getId());
            return new ModelAndView("redirect:/ViewSeries", model);

        } else {
            return new ModelAndView("redirect:/error", model);
        }
    }
}
