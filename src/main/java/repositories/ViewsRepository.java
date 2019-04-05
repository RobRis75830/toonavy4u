package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Views;
import keys.ViewKey;
import java.util.List;

public interface ViewsRepository extends CrudRepository<Views, ViewKey> {

    public List<Views> findByIdViewerAndIdComic(String viewer, int comic);

    public long countByIdComic(int comic);
}
