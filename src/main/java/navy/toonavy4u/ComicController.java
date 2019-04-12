package navy.toonavy4u;

import entities.Comic;
import entities.Pages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import repositories.ComicRepository;
import repositories.PagesRepository;

import javax.sql.rowset.serial.SerialBlob;
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
    public ModelAndView createComic(@ModelAttribute("post") Post post, ModelMap model) {

        String[] imageURL = post.getImageURL();
        Comic comic = post.getComic();

        if (comic.getOwner() == null || comic.getSeries() == 0) {
            return new ModelAndView("redirect:/error", model);
        } else  {
            comic.setCreated(new Timestamp(System.currentTimeMillis()));
            comic = comicRepository.save(comic);

            for (int i = 1; i < imageURL.length; i = i + 2) {
                String images = imageURL[i];

                byte[] bytes = Base64.getDecoder().decode(images);
                Blob image;
                try {
                    image = new SerialBlob(bytes);
                } catch (SQLException e) {
                    return new ModelAndView("redirect:/error", model);
                }
                Pages page = new Pages();
                page.setComic(comic.getId());
                page.setPageNumber((i + 1)/2);
                page.setImage(image);
                pagesRepository.save(page);
            }

            model.addAttribute("comicId", comic.getId());
            return new ModelAndView("redirect:/ViewComic", model);
        }
    }
}
