package de.domesoft.levelupapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.entity.Level;
import de.domesoft.levelupapi.entity.LevelRepository;
import de.domesoft.levelupapi.entity.User;
import de.domesoft.levelupapi.entity.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Component
public class DataParser {
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    UserRepository userRepository;
    @Autowired
    LevelRepository levelRepository;
    public JSONObject postNewLevel(String data) throws Exception {
        JSONObject dataObject = new JSONObject(data);
        JSONObject userObject = dataObject.getJSONObject("user");
        String user = userObject.getString("user_name");
        String password = userObject.getString("password");
        if(userRepository.loginPassed(user, PasswordHash.hash(password)) == 1){
            Level level = mapper.readValue(dataObject.toString(), Level.class);
            User tempUser = userRepository.getUserByName(user);
            level.setUser(tempUser);
            levelRepository.save(level);
            dataObject.remove("user");
            return dataObject;
        }else{
            return new JSONObject();
        }
    }
    public boolean postNewUser(String data) throws Exception {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString("user_name");
        String password = dataObject.getString("password");
        if(userRepository.userExists(user) == 0){
            dataObject.put("password", PasswordHash.hash(password));
            userRepository.save(mapper.readValue(dataObject.toString(), User.class));
            return true;
        }else{
            return false;
        }
    }
    public JSONArray getLevelsByUser(String data) throws Exception {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString("user_name");
        String password = dataObject.getString("password");
        JSONArray levelArray = new JSONArray();
        if(userRepository.loginPassed(user, PasswordHash.hash(password)) == 1){
            List<Level> levelList = levelRepository.getLevel(user);
            for(Level level : levelList){
                JSONObject temp = new JSONObject(mapper.writeValueAsString(level));
                temp.remove("user");
                levelArray.put(temp);
            }
            return levelArray;
        }else{
            return new JSONArray();
        }
    }
    public boolean login(String data) throws Exception{
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString("user_name");
        String password = dataObject.getString("password");
        return userRepository.loginPassed(user, PasswordHash.hash(password)) == 1;
    }
    public JSONObject setExp(String data) throws Exception {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString("user_name");
        String password = dataObject.getString("password");
        String character = dataObject.getString("character");
        Task task = dataObject.getEnum(Task.class, "task");
        JSONObject levelObject;
        if(userRepository.loginPassed(user, PasswordHash.hash(password)) == 1){
            List<Level> levelList = levelRepository.getLevel(user);
            Level level = null;
            for(Level l : levelList){
                if(l.getName().equals(character)){
                    level = l;
                }
            }
            if(level != null) {
                level.setExp(level.getExp() + task.getExp());
                levelRepository.save(level);
                levelObject = new JSONObject(mapper.writeValueAsString(level));
                levelObject.remove("user");
                return levelObject;
            }
            else return new JSONObject();
        }else{
            return new JSONObject();
        }
    }
    public List<Task> getTaskList(){
        return new ArrayList<>(Arrays.asList(Task.class.getEnumConstants()));
    }
}
