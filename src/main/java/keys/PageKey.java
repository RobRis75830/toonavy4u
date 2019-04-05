package keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PageKey implements Serializable {

    private int comic;          // comic ID

    private int pageNumber;     // page number

    public int getComic() { return comic; }

    public void setComic(int comic) { this.comic = comic; }

    public int getPageNumber() { return pageNumber; }

    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
}
