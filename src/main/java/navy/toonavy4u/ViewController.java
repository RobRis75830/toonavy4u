package navy.toonavy4u;

import entities.*;
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

import static navy.toonavy4u.LoginController.getEmail;

@Controller
@EnableAutoConfiguration
public class ViewController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PagesRepository pagesRepository;

    @Autowired
    private ViewsRepository viewsRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @RequestMapping(value = "/ViewComic", method = RequestMethod.GET)
    public String viewComic(@RequestParam("comicId") int comicId, Model model, OAuth2AuthenticationToken authentication) {

        List<Comic> comics = comicRepository.findById(comicId);

        if (!(comics != null && !comics.isEmpty())) {
            return "DoesNotExist";
        }

        List<Series> series = seriesRepository.findById(comics.get(0).getSeries());
        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        if ((comics.get(0).getPublished() == 0 || series.get(0).getPublished() == 0) && !email.equals(comics.get(0).getOwner())) {

            // if this comic is private, and you are not the owner...
            // then you cannot view it
            return "AccessDenied";

        } else {

            List<Pages> pages = pagesRepository.findByIdComic(comicId);
            ArrayList<String> imageURLs = new ArrayList<>();
            List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(comicId);

            for (Pages page : pages) {

                Blob image = page.getImage();
                byte[] bytes;

                try {

                    int blobLength = (int) image.length();
                    bytes = image.getBytes(1, blobLength);
                    image.free();

                } catch (SQLException ex) {
                    return "error";
                }

                String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
                imageURLs.add(imageURL);

            }

            model.addAttribute("seriesId", series.get(0).getId());
            model.addAttribute("series", series.get(0).getTitle());
            model.addAttribute("title", comics.get(0).getTitle());
            model.addAttribute("firstImage", imageURLs.get(0));
            if (imageURLs.size() > 1) {
                model.addAttribute("imageURLs", imageURLs.subList(1, imageURLs.size()));
            } else {
                model.addAttribute("imageURLs", new ArrayList<>());
            }
            model.addAttribute("owner", comics.get(0).getOwner());
            model.addAttribute("comments", comments);
            model.addAttribute("viewer", email);
            model.addAttribute("comicId", comicId);

            if (!email.isEmpty()
                    && viewsRepository.findByIdViewerAndIdComic(email, comicId).isEmpty()
                    && !email.equals(comics.get(0).getOwner())) {

                // if you're logged in, and you haven't seen this comic before, and you're not the owner...
                // then add new view
                Views view = new Views();
                view.setViewer(email);
                view.setComic(comicId);
                viewsRepository.save(view);
            }

            model.addAttribute("post", new Post());

            return "View";
        }
    }
}
