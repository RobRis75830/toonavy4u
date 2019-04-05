package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Comments;
import java.util.List;

public interface CommentsRepository extends CrudRepository<Comments, Integer> {

    public List<Comments> findById(int id);

    public List<Comments> findByComicOrderByCreatedAsc(int comic);
}
