package navy.toonavy4u;

import com.sun.org.apache.xpath.internal.operations.Mod;
import entities.Categories;
import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import repositories.CategoriesRepository;
import repositories.SeriesRepository;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@EnableAutoConfiguration
public class BrowseController {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @RequestMapping(value = "/Browse", method = RequestMethod.GET)
    public String search(@RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "author", required = false) String owner,
                         @RequestParam(value = "categories", required = false) String[] categories,
                         Model model) {

        List<Series> series;

        if (title != null && !title.isEmpty() && owner != null && !owner.isEmpty()) {
            series = seriesRepository.findByTitleIsLikeAndOwnerIsLikeAndPublished("%" + title + "%", "%" + owner + "%", 1);
        } else if (title != null && !title.isEmpty()) {
            series = seriesRepository.findByTitleIsLikeAndPublished("%" + title + "%", 1);
        } else if (owner != null && !owner.isEmpty()) {
            series = seriesRepository.findByOwnerIsLikeAndPublished("%" + owner + "%", 1);
        } else {
            series = seriesRepository.findByPublished(1);
        }

        if (categories != null && categories.length > 0 && series != null && !series.isEmpty()) {
            List<Integer> seriesIds = series.stream().map(Series::getId).collect(Collectors.toList());

            for (String category : categories) {
                List<Categories> c = categoriesRepository.findByIdSeriesIsInAndIdCategory(seriesIds, category);
                if (c != null && !c.isEmpty()) {
                    seriesIds = c.stream().map(Categories::getSeries).collect(Collectors.toList());
                } else {
                    seriesIds = new ArrayList<>();
                    break;
                }
            }

            if (!seriesIds.isEmpty()) {
                series = seriesRepository.findByIdIsIn(seriesIds);
            } else {
                series = new ArrayList<>();
            }
        }

        ArrayList<String> imageURLs = new ArrayList<>();
        for (Series s : series) {
            Blob image = s.getCover();
            byte[] bytes;

            try {

                int blobLength = (int) image.length();
                bytes = image.getBytes(1, blobLength);
                image.free();

            } catch (SQLException ex) {
                return "error";
            }

            String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            imageURLs.add(imageURL);
        }

        if (!series.isEmpty()) {
            model.addAttribute("found", true);
            model.addAttribute("searchResult", series);
            model.addAttribute("imageURLs", imageURLs);
        } else {
            model.addAttribute("found", false);
        }
        return "Browse";
    }
}
