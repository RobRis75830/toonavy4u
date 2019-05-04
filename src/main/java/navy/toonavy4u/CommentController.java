package navy.toonavy4u;

import entities.Comments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import repositories.CommentsRepository;

import java.sql.Timestamp;

@Controller
@EnableAutoConfiguration
public class CommentController {

    @Autowired
    private CommentsRepository commentsRepository;

    @RequestMapping(value = "/makeComment", method = RequestMethod.POST)
    public ModelAndView makeComment(@ModelAttribute("post") Post post, ModelMap model) {

        Comments comment = post.getComment();
        comment.setCreated(new Timestamp(System.currentTimeMillis()));

        commentsRepository.save(comment);

        model.addAttribute("comicId", comment.getComic());
        return new ModelAndView("redirect:/ViewComic", model);
    }

    @RequestMapping(value = "/makeSuggestion", method = RequestMethod.POST)
    public ModelAndView makeSuggestion(@ModelAttribute("post") Post post, ModelMap model) {

        Comments comment = post.getComment();
        comment.setCreated(new Timestamp(System.currentTimeMillis()));

        commentsRepository.save(comment);

        return new ModelAndView("redirect:/ViewFillInTheBlank", model);
    }
}
