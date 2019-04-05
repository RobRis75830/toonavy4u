package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Follows;
import keys.FollowKey;
import java.util.List;

public interface FollowsRepository extends CrudRepository<Follows, FollowKey> {

    public List<Follows> findByIdFollowerAndIdFollowed(String follower, String followed);

    public List<Follows> findByIdFollowed(String followed);
}
