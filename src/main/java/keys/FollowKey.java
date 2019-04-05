package keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FollowKey implements Serializable {

    private String follower;    // user ID of follower

    private String followed;    // ID of user being followed

    public String getFollower() { return follower; }

    public void setFollower(String follower) { this.follower = follower; }

    public String getFollowed() { return followed; }

    public void setFollowed(String followed) { this.followed = followed; }
}
