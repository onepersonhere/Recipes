package recipes;

import com.google.gson.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class controller {
    @GetMapping(value = "/api/recipe/{id}", produces = "application/json")
    @ResponseBody
    public String getRecipeJson(@PathVariable int id){
        Recipe recipe = RecipeStorage.findRecipeByID(id);
        return new Gson().toJson(getRecipeAsJson(recipe));
    }

    @PostMapping(value = "/api/recipe/new", produces = "application/json")
    @ResponseBody
    public String postRecipe(@RequestBody String json){
        int id = parseAddRecipe(json);
        JsonObject jObj = new JsonObject();
        jObj.addProperty("id", id);
        return new Gson().toJson(jObj);
    }

    private JsonObject getRecipeAsJson(Recipe recipe){
        JsonObject jObj = new JsonObject();
        jObj.addProperty("name", recipe.getName());
        jObj.addProperty("description", recipe.getDescription());
        jObj.add("ingredients", getJsonArrFromArr(recipe.getIngredients()));
        jObj.add("directions", getJsonArrFromArr(recipe.getDirections()));
        return jObj;
    }

    private int parseAddRecipe(String json){
        Gson gson = new Gson();
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();
        Recipe recipe = new Recipe();
        recipe.setName(jObj.get("name").getAsString());
        recipe.setDescription(jObj.get("description").getAsString());
        recipe.setIngredients(gson.fromJson(jObj.get("ingredients").getAsJsonArray(), String[].class));
        recipe.setDirections(gson.fromJson(jObj.get("directions").getAsJsonArray(), String[].class));
        recipe.setId(idGenerator());
        RecipeStorage.addToList(recipe);
        return recipe.getId();
    }

    private int idGenerator(){
        return (int)(Math.random() * 999 + 1);
    }

    private JsonArray getJsonArrFromArr(String[] arr){
        JsonArray jArr = new JsonArray();
        for(int i = 0; i < arr.length; i++){
            jArr.add(arr[i]);
        }
        return jArr;
    }
}
