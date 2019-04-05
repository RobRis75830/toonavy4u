package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Reader;
import java.util.List;

public interface ReaderRepository extends CrudRepository<Reader, String> {

    public List<Reader> findByIdtoken(String idtoken);

    public List<Reader> findByIdtokenAndPublished(String idtoken, int published);
}
