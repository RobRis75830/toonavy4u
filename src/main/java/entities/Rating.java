package entities;

import javax.persistence.*;
import java.io.Serializable;
import keys.RateKey;

@Entity
public class Rating implements Serializable {

    @EmbeddedId
    private RateKey id;     // user ID of rater and comic ID

    private int rating;     // rating, 1 - 5

    public String getRater() { return id.getRater(); }

    public void setRater(String rater) { id.setRater(rater); }

    public int getComic() { return id.getComic(); }

    public void setComic(int comic) { id.setComic(comic); }

    public int getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }

    public Rating() {
        id = new RateKey();
    }
}
