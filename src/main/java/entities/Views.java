package entities;

import javax.persistence.*;
import java.io.Serializable;
import keys.ViewKey;

@Entity
public class Views implements Serializable {

    @EmbeddedId
    private ViewKey id;     // user ID of viewer and comic ID

    public String getViewer() { return id.getViewer(); }

    public void setViewer(String viewer) { id.setViewer(viewer); }

    public int getComic() { return id.getComic(); }

    public void setComic(int comic) { id.setComic(comic); }

    public Views() {
        id = new ViewKey();
    }
}
