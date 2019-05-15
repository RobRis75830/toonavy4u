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
import java.util.*;

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
    @Autowired
    private LikesRepository LikesRepository;

    @RequestMapping(value = "/ViewComic", method = RequestMethod.GET)
    public String viewComic(@RequestParam("comicId") int comicId, Model model, OAuth2AuthenticationToken authentication) {

        List<Comic> comics = comicRepository.findById(comicId);

        if (!(comics != null && !comics.isEmpty())) {
            model.addAttribute("errorMessage", "The page you are trying to access has been moved or doesn't exist.");
            return "Error";
        }

        List<Series> series = seriesRepository.findById(comics.get(0).getSeries());
        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        if ((comics.get(0).getPublished() == 0 || series.get(0).getPublished() == 0) && !email.equals(comics.get(0).getOwner())) {

            // if this comic is private, and you are not the owner...
            // then you cannot view it
            model.addAttribute("errorMessage", "You do not have permission to access this page.");
            return "Error";

        } else {

            List<Pages> pages = pagesRepository.findByIdComic(comicId);
            ArrayList<String> imageURLs = new ArrayList<>();
            List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(comicId);
            List<Boolean> likesOrnot= new ArrayList<>();
            List<Long> likesNum= new ArrayList<>();

            for (Pages page : pages) {

                Blob image = page.getImage();
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
                imageURLs.add(imageURL);

            }

            //Likes
            for(int i=0;i<comments.size();i++){
                List<Likes> likes=  LikesRepository.findByIdLikerAndIdRemark(email,comments.get(i).getId());

                likesNum.add(LikesRepository.countByIdRemark(comments.get(i).getId()));
                if (likes.isEmpty()){
                    likesOrnot.add(true);
                }else{
                    likesOrnot.add(false);
                }
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
            model.addAttribute("likesNum", likesNum);
            model.addAttribute("likesOrnot", likesOrnot);
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

    @RequestMapping(value = "/ViewFillInTheBlank", method = RequestMethod.GET)
    public String viewFillInTheBlank(Model model, OAuth2AuthenticationToken authentication) {

        long millis = System.currentTimeMillis() / 604800000;
        long index = millis % 26;
        Comic fillInTheBlank = comicRepository.findBySeriesOrderByCreatedAsc(1202).get((int) index);

        String email = "";
        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        Pages page = pagesRepository.findByIdComicAndIdPageNumber(fillInTheBlank.getId(), 1).get(0);
        List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(fillInTheBlank.getId());
        for (Comments comment : comments) {
            comment.setLikes(LikesRepository.countByIdRemark(comment.getId()));
        }
        comments.sort(Comparator.comparingLong(Comments::getLikes));
        Collections.reverse(comments);

        List<Boolean> likesOrnot= new ArrayList<>();
        List<Long> likesNum= new ArrayList<>();

        Blob image = page.getImage();
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

        //Likes
        for(int i=0;i<comments.size();i++){
            List<Likes> likes=  LikesRepository.findByIdLikerAndIdRemark(email,comments.get(i).getId());
            likesNum.add(LikesRepository.countByIdRemark(comments.get(i).getId()));
            if (likes.isEmpty()){
                likesOrnot.add(true);
            }else{
                likesOrnot.add(false);
            }
        }

        model.addAttribute("title", fillInTheBlank.getTitle());
        model.addAttribute("image", imageURL);
        model.addAttribute("comments", comments);
        model.addAttribute("likesNum", likesNum);
        model.addAttribute("likesOrnot", likesOrnot);
        model.addAttribute("viewer", email);
        model.addAttribute("comicId", fillInTheBlank.getId());
        model.addAttribute("post", new Post());

        return "FillInTheBlanks";
    }
}
