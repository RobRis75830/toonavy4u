package navy.toonavy4u;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableAutoConfiguration
public class HomeController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String newPost(Model model) {
        model.addAttribute("post", new Post());
        return "index";
    }

    @RequestMapping(value = "Front.html", method = RequestMethod.GET)
    public String front(Model model) {
        model.addAttribute("post", new Post());
        return "Front";
    }

    @RequestMapping(value = "Create.html", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("post", new Post());
        return "Create";
    }

    @RequestMapping(value = "Profile.html", method = RequestMethod.GET)
    public String profile(Model model) {
        model.addAttribute("post", new Post());
        return "Profile";
    }

    @RequestMapping(value = "View.html", method = RequestMethod.GET)
    public String view(Model model) {
        model.addAttribute("post", new Post());
        return "View";
    }

}
