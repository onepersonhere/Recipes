package recipes;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeStorage {
    static List<Recipe> recipeList = new ArrayList<>();
    public static void addToList(Recipe recipe){
        recipeList.add(recipe);
    }
    public static Recipe findRecipeByID(int id){
        for(int i = 0; i < recipeList.size(); i++){
            Recipe recipe = recipeList.get(i);
            if(recipe.getId() == id){
                return recipe;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
