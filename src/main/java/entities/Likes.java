package entities;

import javax.persistence.*;
import java.io.Serializable;
import keys.LikeKey;

@Entity
public class Likes implements Serializable {

    @EmbeddedId
    private LikeKey id;     // user ID and comment ID

    public String getLiker() { return id.getLiker(); }

    public void setLiker(String liker) { id.setLiker(liker); }

    public int getRemark() { return id.getRemark(); }

    public void setRemark(int remark) { id.setRemark(remark); }

    public Likes() {
        id = new LikeKey();
    }
}
