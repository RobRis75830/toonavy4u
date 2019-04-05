package entities;

import javax.persistence.*;
import java.sql.Blob;
import java.io.Serializable;
import keys.PageKey;

@Entity
public class Pages implements Serializable {

    @EmbeddedId
    private PageKey id;     // comic ID and page number

    private Blob image;     // image represented by a Binary Large Object

    public int getComic() { return id.getComic(); }

    public void setComic(int comic) { id.setComic(comic); }

    public int getPageNumber() { return id.getPageNumber(); }

    public void setPageNumber(int pageNumber) { id.setPageNumber(pageNumber); }

    public Blob getImage() { return image; }

    public void setImage(Blob image) { this.image = image; }

    public Pages() {
        id = new PageKey();
    }
}
