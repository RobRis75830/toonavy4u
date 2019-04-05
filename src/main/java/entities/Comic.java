package entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.io.Serializable;

@Entity
@TableGenerator(name = "comicidgen")
public class Comic implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator = "comicidgen")
    private int id;             // comic ID

    private String owner;       // user ID of the owner

    private int series;         // series ID

    private String title;       // comic title

    private Timestamp created;  // date created

    private int published;      // boolean value for whether the comic page is publicly viewable, 1 = true, 0 = false

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public int getSeries() { return series; }

    public void setSeries(int series) { this.series = series; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public Timestamp getCreated() { return created; }

    public void setCreated(Timestamp created) { this.created = created; }

    public int getPublished() { return published; }

    public void setPublished(int published) { this.published = published; }
}
