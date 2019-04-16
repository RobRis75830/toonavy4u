package navy.toonavy4u;

import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import repositories.SeriesRepository;

import java.util.List;

import static navy.toonavy4u.LoginController.getEmail;

@Controller
@EnableAutoConfiguration
public class HomeController {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping(value = "/Toolbar.html", method = RequestMethod.GET)
    public String getToolbar(Model model) {
        model.addAttribute("post", new Post());
        return "Toolbar";
    }

    @RequestMapping(value = "/Create.html", method = RequestMethod.GET)
    public String getCreate(Model model, OAuth2AuthenticationToken authentication) {
        String email = getEmail(authentication, authorizedClientService);
        if (!email.isEmpty()) {
            List<Series> series = seriesRepository.findByOwner(email);
            model.addAttribute("seriesRepo", series);
            model.addAttribute("userEmail", email);
            model.addAttribute("editing", false);
        }
        model.addAttribute("post", new Post());
        return "Create";
    }

    @RequestMapping(value = "/Profile.html", method = RequestMethod.GET)
    public String getProfile(Model model) {
        model.addAttribute("post", new Post());
        return "Profile";
    }

    @RequestMapping(value = "/View.html", method = RequestMethod.GET)
    public String getView(Model model) {
        model.addAttribute("post", new Post());
        return "View";
    }

    @RequestMapping(value = "/CreateSeries.html", method = RequestMethod.GET)
    public String getCreateSeries(Model model, OAuth2AuthenticationToken authentication) {
        String email = getEmail(authentication, authorizedClientService);
        if (!email.isEmpty()) {
            model.addAttribute("userEmail", email);
        }
        model.addAttribute("post", new Post());
        return "CreateSeries";
    }

    @RequestMapping(value = "/Browse", method = RequestMethod.GET)
    public String getBrowse(Model model) {
        return "Browse";
    }
}
