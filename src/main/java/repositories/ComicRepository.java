package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Comic;
import java.util.List;

public interface ComicRepository extends CrudRepository<Comic, Integer> {

    public List<Comic> findById(int id);

    public List<Comic> findBySeriesOrderByCreatedDesc(int series);

    public List<Comic> findBySeriesAndPublishedOrderByCreatedDesc(int series, int published);

    public List<Comic> findByPublishedOrderByCreatedDesc(int published);
}
