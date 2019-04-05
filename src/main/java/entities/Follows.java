package entities;

import javax.persistence.*;
import java.io.Serializable;
import keys.FollowKey;

@Entity
public class Follows implements Serializable {

    @EmbeddedId
    private FollowKey id;       // user IDs of the follower and the followed

    public String getFollower() { return id.getFollower(); }

    public void setFollower(String follower) { id.setFollower(follower); }

    public String getFollowed() { return id.getFollowed(); }

    public void setFollowed(String followed) { id.setFollowed(followed); }

    public Follows() {
        id = new FollowKey();
    }
}
