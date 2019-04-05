package entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Reader implements Serializable {

    @Id
    private String idtoken;     // user ID

    private int published;      // boolean value for whether the user's profile is publicly viewable, 1 = true, 0 = false

    public String getIdtoken() { return idtoken; }

    public void setIdtoken(String idtoken) { this.idtoken = idtoken; }

    public int getPublished() { return published; }

    public void setPublished(int published) { this.published = published; }
}
