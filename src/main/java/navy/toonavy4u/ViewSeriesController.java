package navy.toonavy4u;

import entities.Categories;
import entities.Comic;
import entities.Rating;
import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import repositories.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static navy.toonavy4u.LoginController.getEmail;

@Controller
@EnableAutoConfiguration
public class ViewSeriesController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @RequestMapping(value = "/ViewSeries", method = RequestMethod.GET)
    public String viewSeries(@RequestParam("seriesId") int seriesId, Model model, OAuth2AuthenticationToken authentication) {

        List<Series> series = seriesRepository.findById(seriesId);

        if (!(series != null && !series.isEmpty())) {
            model.addAttribute("errorMessage", "The page you are trying to access has been moved or doesn't exist.");
            return "Error";
        }

        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        if (series.get(0).getPublished() == 0 && !email.equals(series.get(0).getOwner())) {

            // if this series is private, and you are not the owner...
            // then you cannot view it
            model.addAttribute("errorMessage", "You do not have permission to access this page.");
            return "Error";

        } else {

            List<Comic> comics = comicRepository.findBySeriesOrderByCreatedAsc(seriesId);
            List<Categories> categoriesRepo = categoriesRepository.findByIdSeries(seriesId);
            String categories = "";
            Blob image = series.get(0).getCover();
            byte[] bytes;

            try {

                int blobLength = (int) image.length();
                bytes = image.getBytes(1, blobLength);
                image.free();

            } catch (SQLException ex) {
                model.addAttribute("errorMessage", "");
                return "Error";
            }

            String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

            if (!email.equals(series.get(0).getOwner())) {
                comics.removeIf(c -> c.getPublished() == 0);
            }

            for (Categories c : categoriesRepo) {
                categories += (" " + c.getCategory());
            }

            // ratings
            ArrayList<Double> comicRatings = new ArrayList<>();
            for (Comic comic : comics) {
                List<Rating> ratings = ratingRepository.findByIdComic(comic.getId());
                double rating = Math.round(ratings.stream().mapToDouble(Rating::getRating).average().orElse(0.0) * 100) / 100.0;
                comicRatings.add(rating);
            }
            ArrayList<Double> comicRatingsClone = new ArrayList<>(comicRatings);
            comicRatingsClone.removeIf(c -> c == 0.0);
            double seriesRating = Math.round(comicRatingsClone.stream().mapToDouble(value -> value).average().orElse(0.0) * 100) / 100.0;

            // subscription info
            if (email.isEmpty()) {
                model.addAttribute("subscribe", 0);         // user not logged in
            } else if (email.equals(series.get(0).getOwner())) {
                model.addAttribute("subscribe", 1);         // user is the owner
            } else if (!subscriptionRepository.findByIdSubscriberAndIdSeries(email, series.get(0).getId()).isEmpty()) {
                model.addAttribute("subscribe", 2);         // user is already subscribed
            } else {
                model.addAttribute("subscribe", 3);         // user is not subscribed
            }

            model.addAttribute("series", series.get(0));
            model.addAttribute("comics", comics);
            model.addAttribute("imageURL", imageURL);
            model.addAttribute("categories", categories);
            model.addAttribute("editable", email.equals(series.get(0).getOwner()));
            model.addAttribute("comicRatings", comicRatings);
            model.addAttribute("seriesRating", seriesRating);

        }

        model.addAttribute("post", new Post());

        return "ViewSeries";
    }
}
