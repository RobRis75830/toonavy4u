package entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.io.Serializable;

@Entity
@TableGenerator(name = "commentidgen")
public class Comments implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator = "commentidgen")
    private int id;                 // comment ID

    private String owner;           // user ID of the owner

    private int comic;              // comic ID

    private String body;            // content of the comment

    private Timestamp created;      // date created

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public int getComic() { return comic; }

    public void setComic(int comic) { this.comic = comic; }

    public String getBody() { return body; }

    public void setBody(String body) { this.body = body; }

    public Timestamp getCreated() { return created; }

    public void setCreated(Timestamp created) { this.created = created; }
}
