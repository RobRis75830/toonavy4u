package navy.toonavy4u;

import entities.Categories;
import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import repositories.CategoriesRepository;
import repositories.SeriesRepository;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Controller
@EnableAutoConfiguration
public class SeriesController {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @RequestMapping(value = "/createSeries", method = RequestMethod.POST)
    public ModelAndView createNewSeries(@ModelAttribute("post") Post post, ModelMap model, OAuth2AuthenticationToken authentication) {

        String image = post.getImageURL()[1];
        Series series = post.getSeries();

        String[] categoryStrings = post.getCategories();

        if (series.getOwner() == null) {
            return new ModelAndView("redirect:/error", model);
        } else {
            byte[] bytes = Base64.getDecoder().decode(image);
            Blob cover;
            try {
                cover = new SerialBlob(bytes);
            } catch (SQLException e) {
                return new ModelAndView("redirect:/error", model);
            }

            series.setCover(cover);
            series.setCreated(new Timestamp(System.currentTimeMillis()));
            series = seriesRepository.save(series);

            if (categoryStrings != null && categoryStrings.length > 0) {
                for (String c : categoryStrings) {
                    Categories category = new Categories();
                    category.setSeries(series.getId());
                    category.setCategory(c);
                    categoriesRepository.save(category);
                }
            }

            model.addAttribute("seriesId", series.getId());
            return new ModelAndView("redirect:/ViewSeries", model);
        }
    }
}
