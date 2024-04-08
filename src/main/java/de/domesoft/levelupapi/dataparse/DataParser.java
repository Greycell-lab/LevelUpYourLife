package de.domesoft.levelupapi.dataparse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.dto.UserDTO;
import de.domesoft.levelupapi.task.Power;
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
    private final String TASK = "task";
    private final String PARENT = "parent";
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    UserRepository userRepository;
    @Autowired
    LevelRepository levelRepository;
    @Autowired
    ParentRepository parentRepository;
    @Autowired
    ObjectMapper objectMapper;

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
    //New with DTO Classes
    public boolean newUser(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if (userRepository.userExists(userDTO.getUser_name()) == UserStatus.USER_NOT_FOUND.getStatus()) {
                User user = new User();
                user.setUser_name(userDTO.getUser_name());
                user.setPassword(PasswordHash.hash(userDTO.getPassword()));
                user.setTasklist("[]");
                user.setPower("[]");
                userRepository.save(user);
                return true;
            }else{
                return false;
            }
        }catch(JsonProcessingException ex){
            ex.printStackTrace();
            return false;
        }catch(NoSuchAlgorithmException ex){
            ex.printStackTrace();
            return false;
        }
    }
    //New with DTO Classes
    public String getLevel(String data){
        try{
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if(userRepository.loginPassed(userDTO.getUser_name(), PasswordHash.hash(userDTO.getPassword())) == 1){
                Level level = levelRepository.getLevel(userDTO.getUser_name());
                level.setUser(null);
                return objectMapper.writeValueAsString(level);
            }else{
                return null;
            }
        }catch(JsonProcessingException ex){
            ex.printStackTrace();
            return null;
        }catch(NoSuchAlgorithmException ex){
            ex.printStackTrace();
            return null;
        }
    }
    public String getLevelByUser(String data) throws NoSuchAlgorithmException, JsonProcessingException {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        if (userRepository.loginPassed(user, PasswordHash.hash(password)) == 1) {
            Level level = levelRepository.getLevel(user);
            JSONArray powers;
            if(userRepository.getPowers(user) == null){
                powers = new JSONArray();
            }else{
                powers = new JSONArray(userRepository.getPowers(user));
            }
            for(Power p : Power.values()){
                if(level.getLevel() >= p.getLevel() && !powers.toString().contains(p.toString())){
                    powers.put(p.toString());
                }
            }
            User userObject = userRepository.getUserByName(user);
            userObject.setPower(powers.toString());
            userRepository.save(userObject);
            JSONObject levelObject = new JSONObject(mapper.writeValueAsString(level));
            levelObject.remove(USER);
            return levelObject.toString();
        } else {
            return "";
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
        Task task = dataObject.getEnum(Task.class, TASK);
        JSONObject levelObject;
        if (userRepository.loginPassed(user, PasswordHash.hash(password)) == 1) {
            Level level;
            level = levelRepository.getLevel(user);
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
        String task = dataObject.getString("task");
        String user = userObject.getString(USERNAME);
        if(userLogin(userObject.toString())){
            User u = userRepository.getUserByName(user);
            JSONArray taskList = new JSONArray(u.getTasklist());
            if(u.getTasklist() == null) {
                u.setTasklist(taskList.toString());
            }
            boolean taskAlreadyInUse = false;
            for(int i=0;i<taskList.length(); i++){
                if(taskList.get(i).equals(task)){
                    taskAlreadyInUse = true;
                }
            }
            if(!taskAlreadyInUse){
                taskList.put(task);
            }
            u.setTasklist(taskList.toString());
            userRepository.save(u);
            return dataObject.toString();
        }
        return "";
    }
    public String getUserTask(String data) throws NoSuchAlgorithmException {
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
    public String acceptTask(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        JSONObject parentObject = dataObject.getJSONObject(PARENT);
        Task acceptedTask = parentObject.getEnum(Task.class,"task");
        String user = parentObject.getString(USERNAME);
        if(parentLogin(parentObject.toString())) {
            Parent parent = parentRepository.getParentFromName(user);
            User userObject = userRepository.getUserFromParent(user);
            String taskList = userObject.getTasklist();
            JSONArray taskArray = new JSONArray(taskList);
            for(int i=0;i<taskArray.length();i++){
                if(taskArray.get(i).equals(acceptedTask.name())){
                    taskArray.remove(i);
                    Level level = levelRepository.getLevel(userObject.getUser_name());
                    level.setExp(level.getExp() + acceptedTask.getExp());
                    if(level.getExp() / 1000 == 1){
                        level.setLevel(level.getLevel() + 1);
                        level.setExp(level.getExp() % 1000);
                    }
                    levelRepository.save(level);
                }
            }
            parent.setTaskList(taskArray.toString());
            userObject.setTasklist(parent.getTaskList());
            parentRepository.save(parent);
            userRepository.save(userObject);
            return parent.getTaskList();
        }
        return "";
    }
    public String getUserPower(String data) throws NoSuchAlgorithmException{
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        if(userRepository.loginPassed(user, PasswordHash.hash(password)) == 1){
            User userObject = userRepository.getUserByName(user);
            return userObject.getPower();
        }else{
            return new JSONArray().toString();
        }
    }
}
