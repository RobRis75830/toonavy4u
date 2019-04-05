package entities;

import javax.persistence.*;
import java.sql.Blob;
import java.sql.Timestamp;
import java.io.Serializable;

@Entity
@TableGenerator(name = "seriesidgen")
public class Series implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator = "seriesidgen")
    private int id;                 // series ID

    private String owner;           // user ID of the series owner

    private String title;           // series title

    private String description;     // description of series

    private Blob cover;             // cover image, represented by a Binary Large Object

    private Timestamp created;      // date when the series was created

    private int published;          // boolean value for whether the series page is publicly viewable, 1 = true, 0 = false

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Blob getCover() { return cover; }

    public void setCover(Blob cover) { this.cover = cover; }

    public Timestamp getCreated() { return created; }

    public void setCreated(Timestamp created) { this.created = created; }

    public int getPublished() { return published; }

    public void setPublished(int published) { this.published = published; }
}
