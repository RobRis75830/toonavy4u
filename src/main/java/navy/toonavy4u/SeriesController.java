package navy.toonavy4u;

import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import repositories.CategoriesRepository;
import repositories.FollowsRepository;
import repositories.SeriesRepository;
import repositories.SubscriptionRepository;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Controller
@EnableAutoConfiguration
public class SeriesController {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private repositories.FollowsRepository FollowsRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @RequestMapping(value = "/createSeries", method = RequestMethod.POST)
    public ModelAndView createNewSeries(@ModelAttribute("post") Post post, ModelMap model, OAuth2AuthenticationToken authentication) {

        String image = post.getImageURL()[1];
        Series series = post.getSeries();

        String[] categoryStrings = post.getCategories();

        if (series.getOwner() == null || !(series.getTitle() != null && !series.getTitle().trim().isEmpty())) {
            return new ModelAndView("redirect:/error", model);
        } else {
            byte[] bytes = Base64.getDecoder().decode(image);
            Blob cover;
            try {
                cover = new SerialBlob(bytes);
            } catch (SQLException e) {
                return new ModelAndView("redirect:/error", model);
            }

            series.setCover(cover);
            if (series.getId() == 0) {
            series.setCreated(new Timestamp(System.currentTimeMillis()));}
            else{
                series.setCreated(seriesRepository.findById(series.getId()).get(0).getCreated());
            }
            series = seriesRepository.save(series);

            if (categoryStrings != null && categoryStrings.length > 0) {
                for (String c : categoryStrings) {
                    Categories category = new Categories();
                    category.setSeries(series.getId());
                    category.setCategory(c);
                    categoriesRepository.save(category);
                }
            }
            List<Follows>followsList=FollowsRepository.findByIdFollowed(series.getOwner());
            for (int i=0;i<followsList.size();i++){
                List <Subscription> sub=subscriptionRepository.findByIdSubscriberAndIdSeries(followsList.get(i).getFollower(),series.getId());
                if (sub.isEmpty()){
                    if (series.getPublished()==1){
                    Subscription newSub=new Subscription();
                    newSub.setSubscriber(followsList.get(i).getFollower());
                    newSub.setSeries(series.getId());
                        subscriptionRepository.save(newSub);}
                }else{
                    if (series.getPublished()==0){
                        subscriptionRepository.delete(sub.get(0)); }
                }

            }


            model.addAttribute("seriesId", series.getId());
            return new ModelAndView("redirect:/ViewSeries", model);
        }
    }

    @RequestMapping(value = "/editSeries", method = {RequestMethod.PUT,RequestMethod.GET, RequestMethod.POST})
    public String editCreate(@ModelAttribute("post") Post post, Model model) {
        int series=post.getSeries().getId();

        Series editseries = seriesRepository.findById(series).get(0);
        List<Categories> categories=categoriesRepository.findByIdSeries(editseries.getId());
        List<String> categori=new ArrayList<String>();
        for (int i=0;i<categories.size();i++){
            categori.add(categories.get(i).getCategory());
        }

        ArrayList<String> blobArray = new ArrayList<>();

            Blob image = editseries.getCover();
            byte[] bytes;
            try {
                int blobLength = (int) image.length();
                bytes = image.getBytes(1, blobLength);
                image.free();
            } catch (SQLException ex) {
                return "error";
            }

            String cover = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);


        model.addAttribute("editseries", editseries);
        model.addAttribute("categories", categori);
        model.addAttribute("cover", cover);
        model.addAttribute("userEmail", editseries.getOwner());
        model.addAttribute("editing", true);
        model.addAttribute("post", new Post());
        return "CreateSeries";
    }

}
