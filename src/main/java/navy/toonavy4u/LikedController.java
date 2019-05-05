package navy.toonavy4u;

import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import repositories.*;

import java.awt.*;
import java.util.List;

import static navy.toonavy4u.LoginController.getEmail;
@Controller
@EnableAutoConfiguration

public class LikedController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private LikesRepository LikesRepository;
    @Autowired
    private CommentsRepository commentsRepository;



    @RequestMapping(value = "/Liked", method = {RequestMethod.PUT,RequestMethod.GET})
    public ModelAndView liked(@RequestParam("commends") int commends, ModelMap model, OAuth2AuthenticationToken authentication) {
        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }
        Likes Like=new Likes();
        Like.setLiker(email);
        Like.setRemark(commends);
        List<Likes> Likes=LikesRepository.findByIdLikerAndIdRemark(email,commends);
        if (Likes.isEmpty()){
            LikesRepository.save(Like);
        }else{
            LikesRepository.delete(Like);
        }
        int comidId=commentsRepository.findById(commends).get(0).getComic();


        model.addAttribute("comicId", comidId);
        return new ModelAndView("redirect:/ViewComic", model);


    }

    @RequestMapping(value = "/Voted", method = {RequestMethod.PUT,RequestMethod.GET})
    public ModelAndView voted(@RequestParam("commends") int commends, ModelMap model, OAuth2AuthenticationToken authentication) {
        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }
        Likes Like=new Likes();
        Like.setLiker(email);
        Like.setRemark(commends);
        List<Likes> Likes=LikesRepository.findByIdLikerAndIdRemark(email,commends);
        if (Likes.isEmpty()){
            LikesRepository.save(Like);
        }else{
            LikesRepository.delete(Like);
        }
        int comidId=commentsRepository.findById(commends).get(0).getComic();

        return new ModelAndView("redirect:/ViewFillInTheBlank", model);


    }

}
