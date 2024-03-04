package de.domesoft.levelupapi.dataparse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.tools.PasswordHash;
import de.domesoft.levelupapi.task.Task;
import de.domesoft.levelupapi.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataParser {
    private final String USERNAME = "user_name";
    private final String USER = "user";
    private final String PASSWORD = "password";
    private final String CHARACTER = "character";
    private final String TASK = "task";
    private final String PARENT = "parent";
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    UserRepository userRepository;
    @Autowired
    LevelRepository levelRepository;
    @Autowired
    ParentRepository parentRepository;

    public JSONObject postNewLevel(String data) throws NoSuchAlgorithmException, JsonProcessingException {
        JSONObject dataObject = new JSONObject(data);
        JSONObject userObject = dataObject.getJSONObject(USER);
        String user = userObject.getString(USERNAME);
        String password = userObject.getString(PASSWORD);
        if (userRepository.loginPassed(user, PasswordHash.hash(password)) == 1) {
            Level level = mapper.readValue(dataObject.toString(), Level.class);
            User tempUser = userRepository.getUserByName(user);
            level.setUser(tempUser);
            levelRepository.save(level);
            dataObject.remove(USER);
            return dataObject;
        } else {
            return new JSONObject();
        }
    }

    public boolean postNewUser(String data) throws NoSuchAlgorithmException, JsonProcessingException {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        if (userRepository.userExists(user) == 0) {
            dataObject.put(PASSWORD, PasswordHash.hash(password));
            userRepository.save(mapper.readValue(dataObject.toString(), User.class));
            return true;
        } else {
            return false;
        }
    }

    public JSONArray getLevelsByUser(String data) throws NoSuchAlgorithmException, JsonProcessingException {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        JSONArray levelArray = new JSONArray();
        if (userRepository.loginPassed(user, PasswordHash.hash(password)) == 1) {
            List<Level> levelList = levelRepository.getLevel(user);
            for (Level level : levelList) {
                JSONObject temp = new JSONObject(mapper.writeValueAsString(level));
                temp.remove(USER);
                levelArray.put(temp);
            }
            return levelArray;
        } else {
            return new JSONArray();
        }
    }

    public boolean userLogin(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        return userRepository.loginPassed(user, PasswordHash.hash(password)) == 1;
    }
    public boolean parentLogin(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        return  parentRepository.loginPassed(user, PasswordHash.hash(password)) == 1;
    }

    public JSONObject setExp(String data) throws NoSuchAlgorithmException, JsonProcessingException {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        String character = dataObject.getString(CHARACTER);
        Task task = dataObject.getEnum(Task.class, TASK);
        JSONObject levelObject;
        if (userRepository.loginPassed(user, PasswordHash.hash(password)) == 1) {
            List<Level> levelList = levelRepository.getLevel(user);
            Level level = null;
            for (Level l : levelList) {
                if (l.getName().equals(character)) {
                    level = l;
                }
            }
            if (level != null) {
                level.setExp(level.getExp() + task.getExp());
                levelRepository.save(level);
                levelObject = new JSONObject(mapper.writeValueAsString(level));
                levelObject.remove(USER);
                return levelObject;
            } else return new JSONObject();
        } else {
            return new JSONObject();
        }
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(Arrays.asList(Task.class.getEnumConstants()));
    }

    public boolean postNewParent(String data) throws NoSuchAlgorithmException, JsonProcessingException {
        JSONObject dataObject = new JSONObject(data);
        String parent = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        JSONObject child = dataObject.getJSONObject(USER);
        String childPassword = child.getString(PASSWORD);
        if (userLogin(child.toString()) && parentRepository.parentExists(parent) == 0) {
            dataObject.put(PASSWORD, PasswordHash.hash(password));
            child.put(PASSWORD, PasswordHash.hash(childPassword));
            dataObject.remove(USER);
            dataObject.put(USER, child);
            Parent parentObject = mapper.readValue(dataObject.toString(), Parent.class);
            parentObject.setUser(userRepository.getUserByName(child.getString(USERNAME)));
            parentRepository.save(parentObject);
            return true;
        } else {
            return false;
        }
    }
    public String startTask(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        JSONObject userObject = dataObject.getJSONObject(USER);
        JSONObject taskList = dataObject.getJSONObject("tasks");
        String user = userObject.getString(USERNAME);
        if(userLogin(userObject.toString())){
            User u = userRepository.getUserByName(user);
            u.setTasklist(taskList.toString());
            userRepository.save(u);
            return dataObject.toString();
        }
        return "";
    }
    public String getUserTaskList(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        JSONObject parentObject = dataObject.getJSONObject(PARENT);
        String user = parentObject.getString(USERNAME);
        if(parentLogin(parentObject.toString())){
            User userObject = userRepository.getUserFromParent(user);
            Parent parent = parentRepository.getParentFromName(user);
            parent.setTaskList(userObject.getTasklist());
            parentRepository.save(parent);
            return parent.getTaskList();
        }else{
            return "";
        }
    }
}
