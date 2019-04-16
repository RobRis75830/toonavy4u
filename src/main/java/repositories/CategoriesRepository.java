package repositories;

import org.springframework.data.repository.CrudRepository;
import entities.Categories;
import keys.CategoryKey;
import java.util.List;

public interface CategoriesRepository extends CrudRepository<Categories, CategoryKey> {

    public List<Categories> findByIdSeries(int series);

    public List<Categories> findByIdCategoryIsIn(List<String> categories);

    public List<Categories> findByIdCategoryNotIn(List<String> categories);

    public List<Categories> findByIdSeriesIsInAndIdCategory(List<Integer> series, String category);
}
