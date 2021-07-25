package recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class controller {
    Recipe recipe = new Recipe();
    @GetMapping(value = "/api/recipe", produces = "application/json")
    @ResponseBody
    public String getRecipeJson(){
        return new Gson().toJson(getRecipeAsJson(recipe));
    }

    @PostMapping(value = "/api/recipe")
    @ResponseBody
    public String postRecipe(@RequestBody String json){
        parseAddRecipe(json);
        return "Received!";
    }

    private JsonObject getRecipeAsJson(Recipe recipe){
        JsonObject jObj = new JsonObject();
        jObj.addProperty("name", recipe.getName());
        jObj.addProperty("description", recipe.getDescription());
        jObj.addProperty("ingredients", recipe.getIngredients());
        jObj.addProperty("directions",recipe.getDirections());
        return jObj;
    }

    private void parseAddRecipe(String json){
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();
        recipe.setName(jObj.get("name").getAsString());
        recipe.setDescription(jObj.get("description").getAsString());
        recipe.setIngredients(jObj.get("ingredients").getAsString());
        recipe.setDirections(jObj.get("directions").getAsString());
    }
}
