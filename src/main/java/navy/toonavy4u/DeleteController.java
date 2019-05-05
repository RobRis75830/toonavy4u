package navy.toonavy4u;

import entities.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import repositories.*;

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

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private RatingRepository ratingRepository;

    // Remember to delete Likes once they're implemented

    @RequestMapping(value = "/DeleteComic", method = RequestMethod.POST)
    public ModelAndView deleteComic(@ModelAttribute("post") Post post, ModelMap model) {

        int comicId = post.getComic().getId();

        List<Views> views = viewsRepository.findByIdComic(comicId);
        List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(comicId);
        List<Pages> pages = pagesRepository.findByIdComic(comicId);
        List<Rating> ratings = ratingRepository.findByIdComic(comicId);
        Comic comic = comicRepository.findById(comicId).get(0);

        int seriesId = comic.getSeries();

        viewsRepository.deleteAll(views);
        commentsRepository.deleteAll(comments);
        pagesRepository.deleteAll(pages);
        ratingRepository.deleteAll(ratings);
        comicRepository.delete(comic);

        model.addAttribute("seriesId", seriesId);
        return new ModelAndView("redirect:/ViewSeries", model);
    }
    @RequestMapping(value = "/DeleteComic.html", method = {RequestMethod.PUT,RequestMethod.GET,RequestMethod.POST})
    public ModelAndView deleteComic1(@ModelAttribute("post") Post post, ModelMap model) {
        int comicId = post.getComic().getId();

        List<Views> views = viewsRepository.findByIdComic(comicId);
        List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(comicId);
        List<Pages> pages = pagesRepository.findByIdComic(comicId);
        List<Rating> ratings = ratingRepository.findByIdComic(comicId);
        Comic comic = comicRepository.findById(comicId).get(0);

        String profileEmail = comic.getOwner();

        viewsRepository.deleteAll(views);
        commentsRepository.deleteAll(comments);
        pagesRepository.deleteAll(pages);
        ratingRepository.deleteAll(ratings);
        comicRepository.delete(comic);

        model.addAttribute("profileEmail", profileEmail);
        return new ModelAndView("redirect:/Profile", model);
    }
    @RequestMapping(value = "/Deleteseries", method = {RequestMethod.PUT,RequestMethod.GET,RequestMethod.POST})
    public ModelAndView deleteSeries(@ModelAttribute("post") Post post, ModelMap model) {
        int seriesId = post.getSeries().getId();
        Series series=seriesRepository.findById(seriesId).get(0);
        List<Comic> comic = comicRepository.findBySeriesOrderByCreatedAsc(seriesId);
        List<Subscription> subscriptions = subscriptionRepository.findByIdSeries(seriesId);
        for (int i=0;i<comic.size();i++){
            List<Views> views = viewsRepository.findByIdComic(comic.get(i).getId());
            List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(comic.get(i).getId());
            List<Pages> pages = pagesRepository.findByIdComic(comic.get(i).getId());
            List<Rating> ratings = ratingRepository.findByIdComic(comic.get(i).getId());
            viewsRepository.deleteAll(views);
            commentsRepository.deleteAll(comments);
            pagesRepository.deleteAll(pages);
            ratingRepository.deleteAll(ratings);}
        String profileEmail = series.getOwner();
        comicRepository.deleteAll(comic);
        subscriptionRepository.deleteAll(subscriptions);
        seriesRepository.delete(series);

        model.addAttribute("profileEmail", profileEmail);
        return new ModelAndView("redirect:/Profile", model);
    }
}
