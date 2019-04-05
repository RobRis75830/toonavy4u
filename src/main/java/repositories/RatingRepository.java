package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Rating;
import keys.RateKey;
import java.util.List;

public interface RatingRepository extends CrudRepository<Rating, RateKey> {

    public List<Rating> findByIdComic(int comic);

    public long countByIdComic(int comic);
}
