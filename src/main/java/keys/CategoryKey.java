package keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CategoryKey implements Serializable {

    private int series;         // series ID

    private String category;    // category

    public int getSeries() { return series; }

    public void setSeries(int series) { this.series = series; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }
}
