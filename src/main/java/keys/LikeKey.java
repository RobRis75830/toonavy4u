package keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class LikeKey implements Serializable {

    private String liker;       // user ID of liker

    private int remark;         // comment ID

    public String getLiker() { return liker; }

    public void setLiker(String liker) { this.liker = liker; }

    public int getRemark() { return remark; }

    public void setRemark(int remark) { this.remark = remark; }
}
