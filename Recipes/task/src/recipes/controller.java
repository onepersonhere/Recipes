package recipes;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.repos.RecipeService;

import java.util.ArrayList;
import java.util.List;


@Controller
public class controller {
    @GetMapping(value = "/api/recipe/{id}", produces = "application/json")
    @ResponseBody
    public String getRecipeJson(@PathVariable int id){
        Import();
        Recipe recipe = findRecipeByID(id);
        return new Gson().toJson(getRecipeAsJson(recipe));
    }

    @PostMapping(value = "/api/recipe/new", produces = "application/json")
    @ResponseBody
    public String postRecipe(@RequestBody String json){
        Import();
        int id = parseAddRecipe(json);
        JsonObject jObj = new JsonObject();
        jObj.addProperty("id", id);
        return new Gson().toJson(jObj);
    }

    @DeleteMapping(value = "/api/recipe/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable int id){
        Import();
        deleteRecipeByID(id);
    }


    @Autowired
    RecipeService service;

    List<Recipe> recipeList = new ArrayList<>();
    public void addToList(Recipe recipe){
        recipeList.add(recipe);
        service.saveNewRecipe(recipe);
    }
    public Recipe findRecipeByID(int id){
        for(int i = 0; i < recipeList.size(); i++){
            Recipe recipe = recipeList.get(i);
            if(recipe.getId() == id){
                return recipe;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    public void deleteRecipeByID(int id){
        Recipe recipe = findRecipeByID(id);
        boolean isRecipe = false;
        for(int i = 0; i < recipeList.size();i++){
            if(recipeList.get(i) == recipe){
                isRecipe = true;
                break;
            }
        }
        if(isRecipe){
            recipeList.remove(recipe);
            service.deleteRecipe(recipe);
        }
    }
    public void Import(){
        recipeList = service.getAllRecipe();
    }

    public int parseAddRecipe(String json){
        Gson gson = new Gson();
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();
        Recipe recipe = new Recipe();
        try{
            if(jObj.get("name").getAsString().isBlank()) throw new Exception();
            if(jObj.get("description").getAsString().isBlank()) throw new Exception();
            if(gson.fromJson(jObj.get("ingredients").getAsJsonArray(), List.class).isEmpty()) throw new Exception();
            if(gson.fromJson(jObj.get("directions").getAsJsonArray(), List.class).isEmpty()) throw new Exception();

            recipe.setName(jObj.get("name").getAsString());
            recipe.setDescription(jObj.get("description").getAsString());
            recipe.setIngredients(gson.fromJson(jObj.get("ingredients").getAsJsonArray(), List.class));
            recipe.setDirections(gson.fromJson(jObj.get("directions").getAsJsonArray(), List.class));
            recipe.setId(idGenerator());
            addToList(recipe);
            return recipe.getId();
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    public JsonObject getRecipeAsJson(Recipe recipe){
        JsonObject jObj = new JsonObject();
        jObj.addProperty("name", recipe.getName());
        jObj.addProperty("description", recipe.getDescription());
        jObj.add("ingredients", getJsonArrFromArr(recipe.getIngredients()));
        jObj.add("directions", getJsonArrFromArr(recipe.getDirections()));
        return jObj;
    }
    private int idGenerator(){
        return (int)(Math.random() * 999 + 1);
    }
    private JsonArray getJsonArrFromArr(List<String> arr){
        JsonArray jArr = new JsonArray();
        for(int i = 0; i < arr.size(); i++){
            jArr.add(arr.get(i));
        }
        return jArr;
    }
}
