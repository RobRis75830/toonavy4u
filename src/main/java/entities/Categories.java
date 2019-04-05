package entities;

import javax.persistence.*;
import java.io.Serializable;
import keys.CategoryKey;

@Entity
public class Categories implements Serializable {

    @EmbeddedId
    private CategoryKey id;     // series ID and category

    public int getSeries() { return id.getSeries(); }

    public void setSeries(int series) { id.setSeries(series); }

    public String getCategory() { return id.getCategory(); }

    public void setCategory(String category) { id.setCategory(category); }

    public Categories() {
        id = new CategoryKey();
    }
}
