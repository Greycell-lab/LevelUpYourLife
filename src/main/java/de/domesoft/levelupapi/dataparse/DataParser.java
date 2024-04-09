package de.domesoft.levelupapi.dataparse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.dto.LevelDTO;
import de.domesoft.levelupapi.dto.ParentDTO;
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

    //New with DTO Classes
    public String postLevel(String data) {
        try {
            LevelDTO levelDTO = objectMapper.readValue(data, LevelDTO.class);
            User user = userRepository.getUserByName(levelDTO.getUser().getUser_name());
            String password = levelDTO.getUser().getPassword();
            if (userRepository.loginPassed(user.getUser_name(), PasswordHash.hash(password)) == 1) {
                Level level = new Level();
                level.setLevel(levelDTO.getLevel());
                level.setName(levelDTO.getName());
                level.setPet(levelDTO.getPet());
                level.setExp(levelDTO.getExp());
                String levelString = objectMapper.writeValueAsString(level);
                level.setUser(user);
                levelRepository.save(level);
                return levelString;
            } else {
                return null;
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //New with DTO Classes
    public boolean addUser(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if (userRepository.userExists(userDTO.getUser_name()) == UserStatus.USER_NOT_FOUND.getStatus()) {
                User user = new User();
                user.setUser_name(userDTO.getUser_name());
                user.setPassword(PasswordHash.hash(userDTO.getPassword()));
                user.setTaskList("[]");
                user.setPower("[]");
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //New with DTO Classes
    public String getLevel(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            User user = userRepository.getUserByName(userDTO.getUser_name());
            if (userRepository.loginPassed(userDTO.getUser_name(), PasswordHash.hash(userDTO.getPassword())) == 1) {
                Level level = levelRepository.getLevelByUserName(userDTO.getUser_name());
                LevelDTO levelDTO = new LevelDTO();
                levelDTO.setLevel(level.getLevel());
                levelDTO.setId(level.getId());
                levelDTO.setName(level.getName());
                levelDTO.setPet(level.getPet());
                levelDTO.setExp(level.getExp());
                levelDTO.setUser(null);
                user.setPower(new JSONArray().toString());
                JSONArray powerArray = new JSONArray();
                for (Power power : Power.values()) {
                    if (level.getLevel() >= power.getLevel() && !user.getPower().contains(power.toString())) {
                        powerArray.put(power.toString());
                    }
                }
                user.setPower(powerArray.toString());
                userRepository.save(user);
                return objectMapper.writeValueAsString(levelDTO);
            } else {
                return null;
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //New with DTO Classes
    public boolean userLogin(String data) throws NoSuchAlgorithmException {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            return userRepository.loginPassed(userDTO.getUser_name(), PasswordHash.hash(userDTO.getPassword())) == 1;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //New with DTO Classes
    public boolean parentLogin(String data) throws NoSuchAlgorithmException {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            return parentRepository.loginPassed(parentDTO.getUser_name(), PasswordHash.hash(parentDTO.getPassword())) == 1;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //New with DTO Classes
    public boolean addParent(String data) {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            User user = userRepository.getUserByName(parentDTO.getUser().getUser_name());
            if (userRepository.loginPassed(user.getUser_name(), parentDTO.getUser().getPassword()) == 1
                    && parentRepository.parentExists(parentDTO.getUser_name()) == 0) {
                Parent parent = new Parent();
                parent.setUser(user);
                parent.setPassword(PasswordHash.hash(parentDTO.getPassword()));
                parent.setUser_name(parentDTO.getUser_name());
                parent.setTaskList("[]");
                parentRepository.save(parent);
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //New with DTO Classes
    //TODO NOT WORKING
    public String startTask(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(new JSONObject(data).getJSONObject("user").toString(), UserDTO.class);
            if (userRepository.loginPassed(userDTO.getUser_name(), PasswordHash.hash(userDTO.getPassword())) == 1) {
                JSONObject dataObject = new JSONObject(data);
                User user = userRepository.getUserByName(userDTO.getUser_name());
                JSONArray tasksDone = dataObject.getJSONArray("tasks");
                JSONArray userTasks = new JSONArray(user.getTaskList());

                if (user.getTaskList() == null) {
                    user.setTaskList(tasksDone.toString());
                } else {
                    for (int i = 0; i < tasksDone.length(); i++) {
                        if (userTasks.toString().contains(tasksDone.get(i).toString())) {
                            break;
                        } else {
                            userTasks.put(tasksDone.get(i));
                        }
                    }
                    user.setTaskList(userTasks.toString());
                    userRepository.save(user);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String startTaskTemp(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        JSONObject userObject = dataObject.getJSONObject(USER);
        String task = dataObject.getString("task");
        String user = userObject.getString(USERNAME);
        if (userLogin(userObject.toString())) {
            User u = userRepository.getUserByName(user);
            JSONArray taskList = new JSONArray(u.getTaskList());
            if (u.getTaskList() == null) {
                u.setTaskList(taskList.toString());
            }
            boolean taskAlreadyInUse = false;
            for (int i = 0; i < taskList.length(); i++) {
                if (taskList.get(i).equals(task)) {
                    taskAlreadyInUse = true;
                    break;
                }
            }
            if (!taskAlreadyInUse) {
                taskList.put(task);
            }
            u.setTaskList(taskList.toString());
            userRepository.save(u);
            return dataObject.toString();
        }
        return "";
    }

    public String getUserTask(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        JSONObject parentObject = dataObject.getJSONObject(PARENT);
        String user = parentObject.getString(USERNAME);
        if (parentLogin(parentObject.toString())) {
            User userObject = userRepository.getUserFromParent(user);
            Parent parent = parentRepository.getParentFromName(user);
            parent.setTaskList(userObject.getTaskList());
            parentRepository.save(parent);
            return parent.getTaskList();
        } else {
            return "";
        }
    }

    public String acceptTask(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        JSONObject parentObject = dataObject.getJSONObject(PARENT);
        Task acceptedTask = parentObject.getEnum(Task.class, "task");
        String user = parentObject.getString(USERNAME);
        if (parentLogin(parentObject.toString())) {
            Parent parent = parentRepository.getParentFromName(user);
            User userObject = userRepository.getUserFromParent(user);
            String taskList = userObject.getTaskList();
            JSONArray taskArray = new JSONArray(taskList);
            for (int i = 0; i < taskArray.length(); i++) {
                if (taskArray.get(i).equals(acceptedTask.name())) {
                    taskArray.remove(i);
                    Level level = levelRepository.getLevelByUserName(userObject.getUser_name());
                    level.setExp(level.getExp() + acceptedTask.getExp());
                    if (level.getExp() / 1000 == 1) {
                        level.setLevel(level.getLevel() + 1);
                        level.setExp(level.getExp() % 1000);
                    }
                    levelRepository.save(level);
                }
            }
            parent.setTaskList(taskArray.toString());
            userObject.setTaskList(parent.getTaskList());
            parentRepository.save(parent);
            userRepository.save(userObject);
            return parent.getTaskList();
        }
        return "";
    }

    public String getUserPower(String data) throws NoSuchAlgorithmException {
        JSONObject dataObject = new JSONObject(data);
        String user = dataObject.getString(USERNAME);
        String password = dataObject.getString(PASSWORD);
        if (userRepository.loginPassed(user, PasswordHash.hash(password)) == 1) {
            User userObject = userRepository.getUserByName(user);
            return userObject.getPower();
        } else {
            return new JSONArray().toString();
        }
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(Arrays.asList(Task.class.getEnumConstants()));
    }
}
