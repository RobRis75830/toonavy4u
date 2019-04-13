package navy.toonavy4u;

import entities.Categories;
import entities.Comic;
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
import repositories.CategoriesRepository;
import repositories.ComicRepository;
import repositories.SeriesRepository;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

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

    @RequestMapping(value = "/ViewSeries", method = RequestMethod.GET)
    public String viewSeries(@RequestParam("seriesId") int seriesId, Model model, OAuth2AuthenticationToken authentication) {

        List<Series> series = seriesRepository.findById(seriesId);

        if (!(series != null && !series.isEmpty())) {
            return "DoesNotExist";
        }

        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        if (series.get(0).getPublished() == 0 && !email.equals(series.get(0).getOwner())) {

            // if this series is private, and you are not the owner...
            // then you cannot view it
            return "AccessDenied";

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
                return "error";
            }

            String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

            if (!email.equals(series.get(0).getOwner())) {
                for (Comic c : comics) {
                    if (c.getPublished() == 0) {
                        comics.remove(c);
                    }
                }
            }

            for (Categories c : categoriesRepo) {
                categories += (" " + c.getCategory());
            }

            model.addAttribute("series", series.get(0));
            model.addAttribute("comics", comics);
            model.addAttribute("imageURL", imageURL);
            model.addAttribute("categories", categories);

        }

        model.addAttribute("post", new Post());

        return "ViewSeries";
    }
}
