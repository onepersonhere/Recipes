package recipes.repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recipes.Recipe;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {
    @Autowired
    Repo repo;

    public List<Recipe> getAllRecipe(){
        List<Recipe> recipeList = new ArrayList<>();
        repo.findAll().forEach(recipe -> recipeList.add(recipe));
        return recipeList;
    }

    public void saveNewRecipe(Recipe recipe){
        repo.save(recipe);
    }

    public void deleteRecipe(Recipe recipe){
        repo.delete(recipe);
    }

    public void replaceRecipe(Recipe recipe){
        deleteRecipe(recipe);
        saveNewRecipe(recipe);
    }
}
