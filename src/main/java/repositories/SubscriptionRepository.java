package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Subscription;
import keys.SubscribeKey;
import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, SubscribeKey> {

    public List<Subscription> findByIdSubscriberAndIdSeries(String subscriber, int series);

    public List<Subscription> findByIdSubscriber(String subscriber);

    public List<Subscription> findByIdSeries(int series);
}
