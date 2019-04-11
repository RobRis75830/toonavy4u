package navy.toonavy4u;

import entities.Comic;
import entities.Pages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import repositories.ComicRepository;
import repositories.PagesRepository;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;

@Controller
@EnableAutoConfiguration
public class ComicController {

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PagesRepository pagesRepository;

    @RequestMapping(value = "/createComic", method = RequestMethod.POST)
    public String createComic(@ModelAttribute("post") Post post, Model model) {

        String[] imageURL = post.getImageURL();
        Comic comic = post.getComic();
        File file = post.getFile();

        if (comic.getOwner() == null || comic.getSeries() == 0) {
            return "error";
        } else if (file == null) {
            comic.setCreated(new Timestamp(System.currentTimeMillis()));
            comic = comicRepository.save(comic);

            for (int i = 1; i < imageURL.length; i = i + 2) {
                String images = imageURL[i];

                byte[] bytes = Base64.getDecoder().decode(images);
                Blob image;
                try {
                    image = new SerialBlob(bytes);
                } catch (SQLException e) {
                    return "error";
                }
                Pages page = new Pages();
                page.setComic(comic.getId());
                page.setPageNumber((i + 1)/2);
                page.setImage(image);
                pagesRepository.save(page);
            }

            model.addAttribute("post", new Post());
            return "Create";
        } else {

            // file upload

            return "Create";
        }
    }
}
