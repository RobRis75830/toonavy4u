package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Likes;
import keys.LikeKey;
import java.util.List;

public interface LikesRepository extends CrudRepository<Likes, LikeKey> {

    public List<Likes> findByIdLikerAndIdRemark(String liker, int remark);

    public long countByIdRemark(int remark);
}
