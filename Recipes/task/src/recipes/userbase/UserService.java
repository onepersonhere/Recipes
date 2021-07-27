package recipes.userbase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recipes.Recipe;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepo repo;

    public List<User> getAllUser(){
        List<User> userList = new ArrayList<>();
        repo.findAll().forEach(recipe -> userList.add(recipe));
        return userList;
    }

    public void saveNewUser(User user){
        repo.save(user);
    }

    public void deleteUser(User user){
        repo.delete(user);
    }

    public void replaceUser(User user){
        deleteUser(user);
        saveNewUser(user);
    }
}
