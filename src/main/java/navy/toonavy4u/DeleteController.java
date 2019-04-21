package navy.toonavy4u;

import entities.Comic;
import entities.Comments;
import entities.Pages;
import entities.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import repositories.ComicRepository;
import repositories.CommentsRepository;
import repositories.PagesRepository;
import repositories.ViewsRepository;

import java.util.List;

@Controller
@EnableAutoConfiguration
public class DeleteController {

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PagesRepository pagesRepository;

    @Autowired
    private ViewsRepository viewsRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    // Remember to delete Likes and Ratings once they're implemented

    @RequestMapping(value = "/DeleteComic", method = RequestMethod.POST)
    public ModelAndView deleteComic(@ModelAttribute("post") Post post, ModelMap model) {

        int comicId = post.getComic().getId();

        List<Views> views = viewsRepository.findByIdComic(comicId);
        List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(comicId);
        List<Pages> pages = pagesRepository.findByIdComic(comicId);
        Comic comic = comicRepository.findById(comicId).get(0);

        int seriesId = comic.getSeries();

        viewsRepository.deleteAll(views);
        commentsRepository.deleteAll(comments);
        pagesRepository.deleteAll(pages);
        comicRepository.delete(comic);

        model.addAttribute("seriesId", seriesId);
        return new ModelAndView("redirect:/ViewSeries", model);
    }
}
