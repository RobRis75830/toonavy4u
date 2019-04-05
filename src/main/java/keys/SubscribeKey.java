package keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SubscribeKey implements Serializable {

    private String subscriber;      // user ID of subscriber

    private int series;             // series ID

    public String getSubscriber() { return subscriber; }

    public void setSubscriber(String subscriber) { this.subscriber = subscriber; }

    public int getSeries() { return series; }

    public void setSeries(int series) { this.series = series; }
}
