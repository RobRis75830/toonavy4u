package navy.toonavy4u;

import entities.Comic;
import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import repositories.ComicRepository;
import repositories.SeriesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static navy.toonavy4u.LoginController.getEmail;

@Controller
@EnableAutoConfiguration
public class RandomController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ComicRepository comicRepository;

    @RequestMapping(value = "/Random", method = RequestMethod.GET)
    public ModelAndView randomSeries(ModelMap model, OAuth2AuthenticationToken authentication) {

        List<Comic> comics;
        if (authentication != null) {
            String email = getEmail(authentication, authorizedClientService);
            comics = comicRepository.findByOwnerNotAndPublished(email, 1);
        } else {
            comics = comicRepository.findByPublishedOrderByCreatedDesc(1);
        }
        Set<Integer> seriesId = comics.stream().map(Comic::getSeries).collect(Collectors.toSet());
        List<Series> series = seriesRepository.findByIdIsInAndPublished(new ArrayList<>(seriesId), 1);
        int randomIndex = (int) (Math.random() * series.size());

        model.addAttribute("seriesId", series.get(randomIndex).getId());
        return new ModelAndView("redirect:/ViewSeries", model);
    }
}
