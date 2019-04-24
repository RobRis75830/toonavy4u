package navy.toonavy4u;

import entities.Comic;
import entities.Pages;
import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import repositories.ComicRepository;
import repositories.PagesRepository;
import repositories.SeriesRepository;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class ComicController {

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PagesRepository pagesRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @RequestMapping(value = "/createComic", method = RequestMethod.POST)
    public ModelAndView createComic(@ModelAttribute("post") Post post, ModelMap model) {

        String[] imageURL = post.getImageURL();
        Comic comic = post.getComic();

        if (comic.getOwner() == null || comic.getSeries() == 0 || !(comic.getTitle() != null && !comic.getTitle().trim().isEmpty())) {
            return new ModelAndView("redirect:/error", model);
        } else  {
            if (comic.getId() == 0) {
                comic.setCreated(new Timestamp(System.currentTimeMillis()));
            } else {
                comic.setCreated(comicRepository.findById(comic.getId()).get(0).getCreated());
            }
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

    @RequestMapping(value = "/Create.html", method = RequestMethod.POST)
    public String postCreate(@ModelAttribute("post") Post post, Model model) {
        Comic editComic = comicRepository.findById(post.getComic().getId()).get(0);
        List<Series> series = seriesRepository.findByOwner(editComic.getOwner());
        List<Pages> pages = pagesRepository.findByIdComic(editComic.getId());
        ArrayList<String> blobArray = new ArrayList<>();
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
            blobArray.add(imageURL);
        }

        model.addAttribute("editComic", editComic);
        model.addAttribute("seriesRepo", series);
        model.addAttribute("blobArray", blobArray);
        model.addAttribute("userEmail", editComic.getOwner());
        model.addAttribute("editing", true);
        model.addAttribute("post", new Post());
        return "Create";
    }

    @RequestMapping(value = "/editComic", method = {RequestMethod.PUT,RequestMethod.GET})
    public String editCreate(@RequestParam("comicId") int comicId, Model model) {
        Comic editComic = comicRepository.findById(comicId).get(0);
        List<Series> series = seriesRepository.findByOwner(editComic.getOwner());
        List<Pages> pages = pagesRepository.findByIdComic(editComic.getId());
        ArrayList<String> blobArray = new ArrayList<>();
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
            blobArray.add(imageURL);
        }

        model.addAttribute("editComic", editComic);
        model.addAttribute("seriesRepo", series);
        model.addAttribute("blobArray", blobArray);
        model.addAttribute("userEmail", editComic.getOwner());
        model.addAttribute("editing", true);
        model.addAttribute("post", new Post());
        return "Create";
    }
}
