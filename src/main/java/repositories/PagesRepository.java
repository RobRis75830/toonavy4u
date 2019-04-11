package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Pages;
import keys.PageKey;
import java.util.List;

public interface PagesRepository extends CrudRepository<Pages, PageKey> {

    public List<Pages> findByIdComicAndIdPageNumber(int comic, int pageNumber);

    public List<Pages> findByIdComic(int comic);
}
