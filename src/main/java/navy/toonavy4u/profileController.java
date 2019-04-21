package navy.toonavy4u;
import com.sun.org.apache.xpath.internal.operations.Mod;
import entities.Categories;
import entities.Comic;
import entities.Pages;
import entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import repositories.CategoriesRepository;
import repositories.ComicRepository;
import repositories.PagesRepository;
import repositories.SeriesRepository;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static navy.toonavy4u.LoginController.getEmail;

@Controller
@EnableAutoConfiguration
public class profileController {
    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ComicRepository comicRepository;
    @Autowired
    private PagesRepository pagesRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping(value = "/myProfile", method = {RequestMethod.PUT,RequestMethod.GET})
    public String myprofile(Model model, OAuth2AuthenticationToken authentication) {
        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }
        String userName="";
        int before=0;
        for (int i=0;i<email.length();i++){
            if (email.charAt(i)== '@') {
                i=email.length();
            }else{
                before+=1;
            } }
        userName=email.substring(0,before);
        List<Series> creatSeries= seriesRepository.findByOwner(email);
        Blob image;
        byte[] bytes;
        List<String> cover = new ArrayList<String>();
        List<List> allcomic = new ArrayList<List>();
        List<List> chapterCover = new ArrayList<List>();
for (int i=0;i<creatSeries.size();i++){
    image = creatSeries.get(i).getCover();
        try {
            int blobLength = (int) image.length();
            bytes = image.getBytes(1, blobLength);
            image.free();

        } catch (SQLException ex) {
            return "error";
        }
        String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        cover.add(imageURL);
        //finsh cover image
    List<Comic> comic= comicRepository.findBySeriesOrderByCreatedAsc(creatSeries.get(i).getId());
    allcomic.add(comic);
    //finish add comic
    List<String> coverChapter = new ArrayList<String>();
    for (int k=0;k<comic.size();k++){
        List<Pages> pages = pagesRepository.findByIdComicAndIdPageNumber(comic.get(k).getId(), 1);
        if (!pages.isEmpty()) {
            Blob chimage = pages.get(0).getImage();
            byte[] chbytes;
            try {
                int blobLength1 = (int) chimage.length();
                chbytes = chimage.getBytes(1, blobLength1);
                chimage.free();

            } catch (SQLException ex) {
                return "error";
            }
            String chiimageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(chbytes);
            coverChapter.add(chiimageURL);
        } else {
            coverChapter.add("");
        }

    }
    chapterCover.add(coverChapter);



}



        model.addAttribute("userName", userName);
        model.addAttribute("creatSeries", creatSeries);
        model.addAttribute("imageURL", cover);
        model.addAttribute("comic", allcomic);
        model.addAttribute("chapterCover", chapterCover);

        model.addAttribute("editable",true);

        return "Profile";
    }


    @RequestMapping(value = "/Profile", method = {RequestMethod.PUT,RequestMethod.GET})
    public String profile(@RequestParam("profileEmail") String profileEmail,Model model, OAuth2AuthenticationToken authentication) {
        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }
        String userName="";
        int before=0;
        for (int i=0;i<profileEmail.length();i++){
            if (profileEmail.charAt(i)== '@') {
                i=profileEmail.length();
            }else{
                before+=1;
            } }
        userName=profileEmail.substring(0,before);
        List<Series> creatSeries=new ArrayList<Series>();
        if (email.equals(profileEmail)){
            creatSeries= seriesRepository.findByOwner(email);

        }else {creatSeries= seriesRepository.findByOwnerIsLikeAndPublished(profileEmail,1);}
        Blob image;
        byte[] bytes;
        List<String> cover = new ArrayList<String>();
        List<List> allcomic = new ArrayList<List>();
        List<List> chapterCover = new ArrayList<List>();
        for (int i=0;i<creatSeries.size();i++){
            image = creatSeries.get(i).getCover();
            try {
                int blobLength = (int) image.length();
                bytes = image.getBytes(1, blobLength);
                image.free();

            } catch (SQLException ex) {
                return "error";
            }
            String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            cover.add(imageURL);
            //finsh cover image
            List<Comic> comic=new ArrayList<Comic>();
            if (email.equals(profileEmail)){
                comic= comicRepository.findBySeriesOrderByCreatedAsc(creatSeries.get(i).getId());
            }
          else {comic= comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(creatSeries.get(i).getId(),1);}
            allcomic.add(comic);
            //finish add comic
            List<String> coverChapter = new ArrayList<String>();
            for (int k=0;k<comic.size();k++){
                List<Pages> pages = pagesRepository.findByIdComicAndIdPageNumber(comic.get(k).getId(), 1);
                if (!pages.isEmpty()) {
                    Blob chimage = pages.get(0).getImage();
                    byte[] chbytes;
                    try {
                        int blobLength1 = (int) chimage.length();
                        chbytes = chimage.getBytes(1, blobLength1);
                        chimage.free();

                    } catch (SQLException ex) {
                        return "error";
                    }
                    String chiimageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(chbytes);
                    coverChapter.add(chiimageURL);
                } else {
                    coverChapter.add("");
                }

            }
            chapterCover.add(coverChapter);



        }



        model.addAttribute("userName", userName);
        model.addAttribute("creatSeries", creatSeries);
        model.addAttribute("imageURL", cover);
        model.addAttribute("comic", allcomic);
        model.addAttribute("chapterCover", chapterCover);
        model.addAttribute("editable",email.equals(profileEmail));

        return "Profile";
    }


}
