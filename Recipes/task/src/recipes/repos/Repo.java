package recipes.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import recipes.Recipe;

@Repository
public interface Repo extends CrudRepository<Recipe, Integer> {
}
