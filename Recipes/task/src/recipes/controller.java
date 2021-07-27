package recipes;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.repos.RecipeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @PutMapping(value = "/api/recipe/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void putRecipe(@RequestBody String json, @PathVariable int id){
        Import();
        updateRecipe(json, id);
    }

    @GetMapping(value = "/api/recipe/search", produces = "application/json")
    @ResponseBody
    public String searchRecipe(@RequestParam(required=false) String category,
                               @RequestParam(required = false) String name){
        Import();
        JsonArray jArr = new JsonArray();
        if(category != null && name == null){
            //search category
            jArr = searchCat(category);
        }
        else if(name != null && category == null){
            //search name
            jArr = searchName(name);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return new Gson().toJson(jArr);
    }

    @Autowired
    RecipeService service;

    List<Recipe> recipeList = new ArrayList<>();
    static String author = "";
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
        if(!recipe.getAuthor().equals(author)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
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
    public void updateRecipe(String json, int id){
        Recipe recipe = findRecipeByID(id);
        if(!recipe.getAuthor().equals(author)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        boolean isRecipe = false;
        int i = 0;
        for(; i < recipeList.size();i++){
            if(recipeList.get(i) == recipe){
                isRecipe = true;
                break;
            }
        }
        if(isRecipe){
            recipe = new Recipe();
            Gson gson = new Gson();
            JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();
            try{
                if(jObj.get("name").getAsString().isBlank()) throw new Exception();
                if(jObj.get("category").getAsString().isBlank()) throw new Exception();
                if(jObj.get("description").getAsString().isBlank()) throw new Exception();
                if(gson.fromJson(jObj.get("ingredients").getAsJsonArray(), List.class).isEmpty()) throw new Exception();
                if(gson.fromJson(jObj.get("directions").getAsJsonArray(), List.class).isEmpty()) throw new Exception();

                recipe.setName(jObj.get("name").getAsString());
                recipe.setCategory(jObj.get("category").getAsString());
                recipe.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSS")));
                recipe.setDescription(jObj.get("description").getAsString());
                recipe.setIngredients(gson.fromJson(jObj.get("ingredients").getAsJsonArray(), List.class));
                recipe.setDirections(gson.fromJson(jObj.get("directions").getAsJsonArray(), List.class));
                recipe.setId(id);
                recipe.setAuthor(author);

                recipeList.set(i, recipe);
                service.replaceRecipe(recipe);
            }catch(Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }
    public JsonArray searchCat(String category){
        JsonArray jArr = new JsonArray();
        List<Recipe> list = new ArrayList<>();
        for(int i = 0; i < recipeList.size(); i++) {
            Recipe recipe = recipeList.get(i);
            if(recipe.getCategory().equalsIgnoreCase(category)){
                list.add(recipe);
            }
        }

        list = sortList(list);
        for(int i = 0; i < list.size();i++){
            JsonObject recipeObj = getRecipeAsJson(list.get(i));
            jArr.add(recipeObj);
        }
        return jArr;
    }
    public JsonArray searchName(String name){
        JsonArray jArr = new JsonArray();
        List<Recipe> list = new ArrayList<>();
        for(int i = 0; i < recipeList.size(); i++) {
            Recipe recipe = recipeList.get(i);
            if(recipe.getName().toLowerCase().contains(name.toLowerCase())){
                list.add(recipe);
            }
        }

        list = sortList(list);
        for(int i = 0; i < list.size();i++){
            JsonObject recipeObj = getRecipeAsJson(list.get(i));
            jArr.add(recipeObj);
        }
        return jArr;
    }

    public int parseAddRecipe(String json){
        Gson gson = new Gson();
        JsonObject jObj = new JsonParser().parse(json).getAsJsonObject();
        Recipe recipe = new Recipe();
        try{
            if(jObj.get("name").getAsString().isBlank()) throw new Exception();
            if(jObj.get("category").getAsString().isBlank()) throw new Exception();
            if(jObj.get("description").getAsString().isBlank()) throw new Exception();
            if(gson.fromJson(jObj.get("ingredients").getAsJsonArray(), List.class).isEmpty()) throw new Exception();
            if(gson.fromJson(jObj.get("directions").getAsJsonArray(), List.class).isEmpty()) throw new Exception();

            recipe.setName(jObj.get("name").getAsString());
            recipe.setCategory(jObj.get("category").getAsString());
            recipe.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSS")));
            recipe.setDescription(jObj.get("description").getAsString());
            recipe.setIngredients(gson.fromJson(jObj.get("ingredients").getAsJsonArray(), List.class));
            recipe.setDirections(gson.fromJson(jObj.get("directions").getAsJsonArray(), List.class));
            recipe.setId(idGenerator());
            recipe.setAuthor(author);
            addToList(recipe);
            return recipe.getId();
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    public JsonObject getRecipeAsJson(Recipe recipe){
        JsonObject jObj = new JsonObject();
        jObj.addProperty("name", recipe.getName());
        jObj.addProperty("category", recipe.getCategory());
        jObj.addProperty("date", recipe.getDate());
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
    private List<Recipe> sortList(List<Recipe> list){
        List<Recipe> sortedList = new ArrayList<>();
        List<LocalDateTime> dateTimeList = new ArrayList<>();
        //Extract dateTime
        for(int i = 0; i < list.size(); i++){
            Recipe recipe = list.get(i);
            LocalDateTime dateTime =
                    LocalDateTime.parse(
                            recipe.getDate(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSS")
                    );
            dateTimeList.add(dateTime);
        }
        //sort dateTime by latest first
        for(int i = 0; i < dateTimeList.size();i++){
            for(int j = i + 1; j < dateTimeList.size();j++) {
                LocalDateTime current = dateTimeList.get(i);
                LocalDateTime next = dateTimeList.get(j);
                if (current.isBefore(next)) {
                    dateTimeList.set(i, next);
                    dateTimeList.set(j, current);
                }
            }
        }
        //fill the sortedList
        for(int i = 0; i < dateTimeList.size();i++){
            LocalDateTime current = dateTimeList.get(i);
            for(int j = 0; j < list.size(); j++){
                Recipe recipe = list.get(j);
                LocalDateTime dateTime =
                        LocalDateTime.parse(
                                recipe.getDate(),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSS")
                        );
                if(current.isEqual(dateTime)){
                    sortedList.add(recipe);
                }
            }
        }
        return sortedList;
    }

    public static void setAuthor(String author) {
        controller.author = author;
    }
}
