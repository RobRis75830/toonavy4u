package navy.toonavy4u;
import com.sun.org.apache.xpath.internal.operations.Mod;
import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;
import repositories.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    private ComicRepository comicRepository;
    @Autowired
    private PagesRepository pagesRepository;
    @Autowired
    private FollowsRepository FollowsRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ViewsRepository ViewsRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping(value = "/myProfile", method = {RequestMethod.PUT,RequestMethod.GET})
    public String myprofile(Model model, OAuth2AuthenticationToken authentication) {
        String email = "";

        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }
        String userName=email;


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
            model.addAttribute("errorMessage", "");
            return "Error";
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
                model.addAttribute("errorMessage", "");
                return "Error";
            }
            String chiimageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(chbytes);
            coverChapter.add(chiimageURL);
        } else {
            coverChapter.add("");
        }

    }
    chapterCover.add(coverChapter);




}
        List<Follows>  follows=FollowsRepository.findByIdFollowed(email);

        int followNumber=follows.size();



        //SUBSCRBELIST
         List<Subscription> subSeries=subscriptionRepository.findByIdSubscriber( email);
        List<String> subcover = new ArrayList<String>();
        List<List> suballcomic = new ArrayList<List>();
        List<List> subchapterCover = new ArrayList<List>();
        List<Series> subedseries= new ArrayList<Series>();
        List<Boolean> update= new ArrayList<Boolean>();
        for (int i=0;i<subSeries.size();i++){
            subedseries.add(seriesRepository.findById(subSeries.get(i).getSeries()).get(0));
            image = subedseries.get(i).getCover();
            try {
                int blobLength = (int) image.length();
                bytes = image.getBytes(1, blobLength);
                image.free();

            } catch (SQLException ex) {
                model.addAttribute("errorMessage", "");
                return "Error";
            }
            String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            subcover.add(imageURL);
            //finsh subcover image
            List<Comic> comic= comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(subedseries.get(i).getId(),1);
            suballcomic.add(comic);
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
                        model.addAttribute("errorMessage", "");
                        return "Error";
                    }
                    String chiimageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(chbytes);
                    coverChapter.add(chiimageURL);
                } else {
                    coverChapter.add("");
                }

            }
            subchapterCover.add(coverChapter);
        //add check update
            update.add(updateNew(email,subSeries.get(i).getSeries()));
        }
        //follow list

        List<Follows>  follow=FollowsRepository.findByIdFollower(email);
       List<String> follower=  new ArrayList<String>();
       for (int i=0;i<follow.size();i++){
           follower.add(follow.get(i).getFollowed());
       }





        model.addAttribute("userName", userName);
        model.addAttribute("creatSeries", creatSeries);
        model.addAttribute("imageURL", cover);
        model.addAttribute("subedseries", subedseries);
        model.addAttribute("suballcomic", suballcomic);
        model.addAttribute("chapterCover", chapterCover);
        model.addAttribute("subchapterCover", subchapterCover);
        model.addAttribute("subcover", subcover);
        model.addAttribute("comics", allcomic);
        model.addAttribute("followNumber", followNumber);
        model.addAttribute("editable",true);
        model.addAttribute("update",update);
        model.addAttribute("follower",follower);
        model.addAttribute("post", new Post());

        return "Profile";
    }


    @RequestMapping(value = "/Profile", method = {RequestMethod.PUT,RequestMethod.GET})
    public String profile(@RequestParam("profileEmail") String profileEmail,Model model, OAuth2AuthenticationToken authentication) {
        String email = "";


        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        List<Follows> follows=FollowsRepository.findByIdFollowerAndIdFollowed(email,profileEmail);

        boolean follow=false;
        if (follows.isEmpty()){
            follow=false;
        }else{
            follow=true;
        }
        follows=FollowsRepository.findByIdFollowed(profileEmail);

        int followNumber=follows.size();


        String userName=profileEmail;
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
                model.addAttribute("errorMessage", "");
                return "Error";
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
                        model.addAttribute("errorMessage", "");
                        return "Error";
                    }
                    String chiimageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(chbytes);
                    coverChapter.add(chiimageURL);
                } else {
                    coverChapter.add("");
                }

            }
            chapterCover.add(coverChapter);




        }

        //SUBSCRBELIST
        List<Subscription> subSeries=subscriptionRepository.findByIdSubscriber(profileEmail);
        List<String> subcover = new ArrayList<String>();
        List<List> suballcomic = new ArrayList<List>();
        List<List> subchapterCover = new ArrayList<List>();
        List<Series> subedseries= new ArrayList<Series>();
        List<Boolean> update= new ArrayList<Boolean>();
        for (int i=0;i<subSeries.size();i++){
            subedseries.add(seriesRepository.findByIdAndPublished(subSeries.get(i).getSeries(), 1).get(0));
            image = subedseries.get(i).getCover();
            try {
                int blobLength = (int) image.length();
                bytes = image.getBytes(1, blobLength);
                image.free();

            } catch (SQLException ex) {
                model.addAttribute("errorMessage", "");
                return "Error";
            }
            String imageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            subcover.add(imageURL);
            //finsh subcover image
            List<Comic> comic= comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(subedseries.get(i).getId(),1);
            suballcomic.add(comic);
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
                        model.addAttribute("errorMessage", "");
                        return "Error";
                    }
                    String chiimageURL = "data:image/png;base64," + Base64.getEncoder().encodeToString(chbytes);
                    coverChapter.add(chiimageURL);
                } else {
                    coverChapter.add("");
                }

            }
            subchapterCover.add(coverChapter);
            //add check update
            update.add(updateNew(email,subSeries.get(i).getSeries()));

        }
        //follow list

        List<Follows>  followed=FollowsRepository.findByIdFollower(email);
        List<String> follower=  new ArrayList<String>();
        for (int i=0;i<followed.size();i++){
            follower.add(followed.get(i).getFollowed());
        }







        model.addAttribute("userName", userName);
        model.addAttribute("creatSeries", creatSeries);
        model.addAttribute("imageURL", cover);
        model.addAttribute("comics", allcomic);
        model.addAttribute("chapterCover", chapterCover);
        model.addAttribute("editable",email.equals(profileEmail));
        model.addAttribute("follow",follow);
        model.addAttribute("followNumber",followNumber);
        model.addAttribute("subedseries", subedseries);
        model.addAttribute("suballcomic", suballcomic);
        model.addAttribute("subchapterCover", subchapterCover);
        model.addAttribute("subcover", subcover);
        model.addAttribute("update",update);
        model.addAttribute("follower",follower);
        model.addAttribute("post", new Post());

        return "Profile";
    }


    @RequestMapping(value = "/follows", method = {RequestMethod.PUT,RequestMethod.GET})
    public ModelAndView follows(@RequestParam("profileEmail") String profileEmail, ModelMap model, OAuth2AuthenticationToken authentication) {
        String email = "";


        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        Follows follow =  new Follows();
        follow.setFollower(email);
        follow.setFollowed(profileEmail);

        List<Follows> follows=FollowsRepository.findByIdFollowerAndIdFollowed(email,follow.getFollowed());
        boolean isfollow=false;
        if (follows.isEmpty()){
            isfollow=false;
        }else{
            isfollow=true;
        }
        List<Series> seriessub=seriesRepository.findByOwnerIsLikeAndPublished(profileEmail, 1);
        Subscription sub=new Subscription();

        if (isfollow){
            FollowsRepository.delete(follow);
            for (int i=0;i<seriessub.size();i++){
                sub.setSeries(seriessub.get(i).getId());
                sub.setSubscriber(email);
                subscriptionRepository.delete(sub);
            }


        }else{
            FollowsRepository.save(follow);
            for (int i=0;i<seriessub.size();i++){
                sub.setSeries(seriessub.get(i).getId());
                sub.setSubscriber(email);
                subscriptionRepository.save(sub);
                List<Comic> comic= comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(seriessub.get(i).getId(),1);
                for (int k=0;k<comic.size();k++){
                    if (ViewsRepository.findByIdViewerAndIdComic(email,comic.get(k).getId()).isEmpty()){
                        Views view=new Views();
                        view.setComic(comic.get(k).getId());
                        view.setViewer(email);
                        ViewsRepository.save(view);
                    } }
            }

        }
        model.addAttribute("profileEmail", profileEmail);
        return new ModelAndView("redirect:/Profile", model);
    }


    @RequestMapping(value = "/followed", method = {RequestMethod.PUT,RequestMethod.GET})
    public ModelAndView followed(@RequestParam("profileEmail") String profileEmail, ModelMap model, OAuth2AuthenticationToken authentication) {
        String email = "";
        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }

        Follows follow =  new Follows();
        follow.setFollower(email);
        follow.setFollowed(profileEmail);

        List<Follows> follows=FollowsRepository.findByIdFollowerAndIdFollowed(email,follow.getFollowed());
        boolean isfollow=false;
        if (follows.isEmpty()){
            isfollow=false;
        }else{
            isfollow=true;
        }
        List<Series> seriessub=seriesRepository.findByOwnerIsLikeAndPublished(profileEmail, 1);
        Subscription sub=new Subscription();

        if (isfollow){
            FollowsRepository.delete(follow);
            for (int i=0;i<seriessub.size();i++){
                sub.setSeries(seriessub.get(i).getId());
                sub.setSubscriber(email);
                subscriptionRepository.delete(sub);
            }


        }else{
            FollowsRepository.save(follow);
            for (int i=0;i<seriessub.size();i++){
                sub.setSeries(seriessub.get(i).getId());
                sub.setSubscriber(email);
                subscriptionRepository.save(sub);
                List<Comic> comic= comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(seriessub.get(i).getId(),1);
                for (int k=0;k<comic.size();k++){
                    if (ViewsRepository.findByIdViewerAndIdComic(email,comic.get(k).getId()).isEmpty()){
                        Views view=new Views();
                        view.setComic(comic.get(k).getId());
                        view.setViewer(email);
                        ViewsRepository.save(view);
                    } }
            }

        }
        model.addAttribute("profileEmail", email);
        return new ModelAndView("redirect:/Profile", model);
    }





    @RequestMapping(value = "/sub", method = {RequestMethod.PUT,RequestMethod.GET})
    public ModelAndView sub(@RequestParam("series") int series, ModelMap model, OAuth2AuthenticationToken authentication) {
        String email = "";


        if (authentication != null) {
            email = getEmail(authentication, authorizedClientService);
        }
        Subscription sub=new Subscription();
        sub.setSeries(series);
        sub.setSubscriber(email);
        subscriptionRepository.delete(sub);

        model.addAttribute("profileEmail", email);
        return new ModelAndView("redirect:/Profile", model);


    }


    public boolean updateNew(String user, int series){
        List<Comic> comic= comicRepository.findBySeriesAndPublishedOrderByCreatedDesc(series,1);
        for (int i=0;i<comic.size();i++){
            if (ViewsRepository.findByIdViewerAndIdComic(user,comic.get(i).getId()).isEmpty()){
                return true;
            } }
        return false;
    }


}
