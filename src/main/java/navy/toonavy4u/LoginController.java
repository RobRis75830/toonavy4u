package navy.toonavy4u;

import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import repositories.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("email")
public class LoginController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ViewsRepository viewsRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PagesRepository pagesRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {
        if (authentication != null) {
            String email = getEmail(authentication, authorizedClientService);

            if (!email.isEmpty()) {
                List<Reader> readers = readerRepository.findByIdtoken(email);
                Reader reader;

                if (readers.isEmpty()) {                // if reader is not in database
                    reader = new Reader();
                    reader.setIdtoken(email);
                    reader.setPublished(0);

                    readerRepository.save(reader);
                }
            }
        }

        // most popular
        List<Series> series = seriesRepository.findByPublished(1);
        for (Series s : series) {
            List<Comic> comics = comicRepository.findBySeriesOrderByCreatedAsc(s.getId());
            List<Integer> comicIds = comics.stream().map(Comic::getId).collect(Collectors.toList());
            long views = viewsRepository.countByIdComicIsIn(comicIds);
            s.setViews(views);

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
        series.sort(Comparator.comparingDouble(s -> s.getViews() * (s.getRating() + 1)));
        if (series.size() > 6) {
            series = series.subList(series.size() - 6, series.size());
        }
        Collections.reverse(series);

        ArrayList<String> imageURLs = new ArrayList<>();
        for (Series s : series) {
            Blob image = s.getCover();
            byte[] bytes;
            try {
                int blobLength = (int) image.length();
                bytes = image.getBytes(1, blobLength);
                image.free();
            } catch (SQLException ex) {
                model.addAttribute("errorMessage", "");
                return "Error";
            }
            String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            imageURLs.add(imageURL);
        }

        // Fill in the Blanks
        long millis = System.currentTimeMillis() / 604800000;
        long index = millis % 26;
        Comic fillInTheBlank = comicRepository.findBySeriesOrderByCreatedAsc(1202).get((int) index);
        List<Comments> comments = commentsRepository.findByComicOrderByCreatedAsc(fillInTheBlank.getId());
        for (Comments comment : comments) {
            comment.setLikes(likesRepository.countByIdRemark(comment.getId()));
        }
        comments.sort(Comparator.comparingLong(Comments::getLikes));
        if (comments.size() > 3) {
            comments = comments.subList(comments.size() - 3, comments.size());
        }
        Collections.reverse(comments);
        Blob image = pagesRepository.findByIdComicAndIdPageNumber(fillInTheBlank.getId(), 1).get(0).getImage();
        byte[] bytes;
        try {
            int blobLength = (int) image.length();
            bytes = image.getBytes(1, blobLength);
            image.free();
        } catch (SQLException ex) {
            model.addAttribute("errorMessage", "");
            return "Error";
        }
        String fillInTheBlankImage = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        model.addAttribute("popular", series);
        model.addAttribute("imageURLs", imageURLs);
        model.addAttribute("fillInTheBlankImage", fillInTheBlankImage);
        model.addAttribute("comments", comments);
        model.addAttribute("post", new Post());
        return "Front";
    }

    public static String getEmail(OAuth2AuthenticationToken authentication, OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String userInfoEndpointUri = client
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        String email = "";
        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());

            HttpEntity<String> entity = new HttpEntity<>("", headers);
            ResponseEntity<Map> response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            if (userAttributes != null && userAttributes.get("email") != null) {
                email = userAttributes.get("email").toString();
            }
        }

        return email;
    }
}
