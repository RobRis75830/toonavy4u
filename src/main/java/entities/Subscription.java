package entities;

import javax.persistence.*;
import java.io.Serializable;
import keys.SubscribeKey;

@Entity
public class Subscription implements Serializable {

    @EmbeddedId
    private SubscribeKey id;        // user ID of subscriber and series ID

    public String getSubscriber() { return id.getSubscriber(); }

    public void setSubscriber(String subscriber) { id.setSubscriber(subscriber); }

    public int getSeries() { return id.getSeries(); }

    public void setSeries(int series) { id.setSeries(series); }

    public Subscription() {
        id = new SubscribeKey();
    }
}
