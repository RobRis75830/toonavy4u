package keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RateKey implements Serializable {

    private String rater;       // user ID of rater

    private int comic;          // comic ID

    public String getRater() { return rater; }

    public void setRater(String rater) { this.rater = rater; }

    public int getComic() { return comic; }

    public void setComic(int comic) { this.comic = comic; }
}
