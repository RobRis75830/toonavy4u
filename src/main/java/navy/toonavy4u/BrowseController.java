package navy.toonavy4u;

import entities.Categories;
import entities.Comic;
import entities.Rating;
import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import repositories.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@EnableAutoConfiguration
public class BrowseController {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ViewsRepository viewsRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @RequestMapping(value = "/Browse", method = RequestMethod.GET)
    public String search(@RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "author", required = false) String owner,
                         @RequestParam(value = "categories", required = false) String[] categories,
                         @RequestParam(value = "sortBy", required = false) String sortBy,
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

        // sorting
        if (sortBy != null && !series.isEmpty()) {
            List<Comic> comics;
            if (sortBy.equals("views")) {
                for (Series s : series) {
                    comics = comicRepository.findBySeriesOrderByCreatedAsc(s.getId());
                    List<Integer> comicIds = comics.stream().map(Comic::getId).collect(Collectors.toList());
                    long views = viewsRepository.countByIdComicIsIn(comicIds);
                    s.setViews(views);
                }
                series.sort(Comparator.comparingLong(Series::getViews));

            } else if (sortBy.equals("ratings")) {
                for (Series s : series) {
                    comics = comicRepository.findBySeriesOrderByCreatedAsc(s.getId());
                    ArrayList<Double> comicRatings = new ArrayList<>();
                    for (Comic c : comics) {
                        List<Rating> ratings = ratingRepository.findByIdComic(c.getId());
                        double rating = Math.round(ratings.stream().mapToDouble(Rating::getRating).average().orElse(0.0) * 100) / 100.0;
                        if (rating != 0.0) {
                            comicRatings.add(rating);
                        }
                    }
                    double seriesRating = Math.round(comicRatings.stream().mapToDouble(value -> value).average().orElse(0.0) * 100) / 100.0;
                    s.setRating(seriesRating);
                }
                series.sort(Comparator.comparingDouble(Series::getRating));

            } else if (sortBy.equals("updated")) {
                for (Series s : series) {
                    comics = comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(s.getId(), 1);
                    if (!comics.isEmpty()) {
                        s.setUpdated(comics.get(0).getCreated());
                    } else {
                        s.setUpdated(new Timestamp(0));
                    }
                }
                series.sort(Comparator.comparing(Series::getUpdated));
            }
            Collections.reverse(series);
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
