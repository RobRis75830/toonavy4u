package navy.toonavy4u;

import entities.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import repositories.RatingRepository;

@Controller
@EnableAutoConfiguration
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;

    @RequestMapping(value = "/Rate", method = RequestMethod.POST)
    public ModelAndView rateComic(@ModelAttribute("post") Post post, ModelMap model) {

        if (post.getRating() != null && post.getRating().getRating() != 0) {

            ratingRepository.save(post.getRating());
            model.addAttribute("comicId", post.getRating().getComic());
            return new ModelAndView("redirect:/ViewComic", model);
        }

        return new ModelAndView("redirect:/error", model);
    }
}
