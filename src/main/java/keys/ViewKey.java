package keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ViewKey implements Serializable {

    private String viewer;      // user ID of viewer

    private int comic;          // comic ID

    public String getViewer() { return viewer; }

    public void setViewer(String viewer) { this.viewer = viewer; }

    public int getComic() { return comic; }

    public void setComic(int comic) { this.comic = comic; }
}
