package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Series;
import java.util.List;

public interface SeriesRepository extends CrudRepository<Series, Integer> {

    public List<Series> findById(int series);

    public List<Series> findByIdAndPublished(int series, int published);

    public List<Series> findByOwner(String owner);

    public List<Series> findByTitleIsLikeAndPublished(String title, int published); // parameter 'title' should be formatted like "%title%"

    public List<Series> findByTitleIsLikeAndPublishedOrderByCreatedDesc(String title, int published); // parameter 'title' should be formatted like "%title%"
}
