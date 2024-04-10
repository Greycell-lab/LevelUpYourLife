package de.domesoft.levelupapi.dataparse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.dto.LevelDTO;
import de.domesoft.levelupapi.dto.ParentDTO;
import de.domesoft.levelupapi.dto.UserDTO;
import de.domesoft.levelupapi.dto.UserTaskDTO;
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
    UserRepository userRepository;
    LevelRepository levelRepository;
    ParentRepository parentRepository;
    ObjectMapper objectMapper;
    @Autowired
    public DataParser(UserRepository userRepository,
                      LevelRepository levelRepository,
                      ParentRepository parentRepository,
                      ObjectMapper objectMapper){
        this.userRepository = userRepository;
        this.levelRepository = levelRepository;
        this.parentRepository = parentRepository;
        this.objectMapper = objectMapper;
    }

    //New with DTO Classes
    public String postLevel(String data) {
        try {
            LevelDTO levelDTO = objectMapper.readValue(data, LevelDTO.class);
            User user = userRepository.getUserByName(levelDTO.getUser().getUserName());
            String password = levelDTO.getUser().getPassword();
            if (userRepository.loginPassed(user.getUserName(), PasswordHash.hash(password)) == 1) {
                Level level = new Level();
                level.setPetLevel(levelDTO.getLevel());
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
            if (userRepository.userExists(userDTO.getUserName()) == 0) {
                User user = new User();
                user.setUserName(userDTO.getUserName());
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
            User user = userRepository.getUserByName(userDTO.getUserName());
            if (userRepository.loginPassed(userDTO.getUserName(), PasswordHash.hash(userDTO.getPassword())) == 1) {
                Level level = levelRepository.getLevelByUserName(userDTO.getUserName());
                LevelDTO levelDTO = new LevelDTO();
                levelDTO.setLevel(level.getPetLevel());
                levelDTO.setId(level.getId());
                levelDTO.setName(level.getName());
                levelDTO.setPet(level.getPet());
                levelDTO.setExp(level.getExp());
                levelDTO.setUser(null);
                user.setPower(new JSONArray().toString());
                JSONArray powerArray = new JSONArray();
                for (Power power : Power.values()) {
                    if (level.getPetLevel() >= power.getLevel() && !user.getPower().contains(power.toString())) {
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
            return userRepository.loginPassed(userDTO.getUserName(), PasswordHash.hash(userDTO.getPassword())) == 1;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //New with DTO Classes
    public boolean parentLogin(String data) throws NoSuchAlgorithmException {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            return parentRepository.loginPassed(parentDTO.getUserName(), PasswordHash.hash(parentDTO.getPassword())) == 1;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //Overloaded Method
    public boolean parentLogin(ParentDTO parentDTO) {
        try {
            return parentRepository.loginPassed(parentDTO.getUserName(), PasswordHash.hash(parentDTO.getPassword())) == 1;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //New with DTO Classes
    public boolean addParent(String data) {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            User user = userRepository.getUserByName(parentDTO.getUser().getUserName());
            if (userRepository.loginPassed(user.getUserName(), PasswordHash.hash(parentDTO.getUser().getPassword())) == 1
                    && parentRepository.parentExists(parentDTO.getUserName()) == 0) {
                Parent parent = new Parent();
                parent.setUser(user);
                parent.setPassword(PasswordHash.hash(parentDTO.getPassword()));
                parent.setUserName(parentDTO.getUserName());
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
    public String startTask(String data) {
        try {
            UserTaskDTO userTaskDTO = objectMapper.readValue(data, UserTaskDTO.class);
            if (userRepository.loginPassed(userTaskDTO.getUserName(), PasswordHash.hash(userTaskDTO.getPassword())) == 1) {
                User user = userRepository.getUserByName(userTaskDTO.getUserName());
                JSONArray userTaskArray = new JSONArray(user.getTaskList());
                List<String> postedTasks = userTaskDTO.getDoneTasks();
                if (user.getTaskList() == null) {
                    user.setTaskList(postedTasks.toString());
                } else {
                    for (String postedTask : postedTasks) {

                        if (!userTaskArray.toString().contains(postedTask)) {
                            userTaskArray.put(postedTask);
                        }
                    }
                    user.setTaskList(userTaskArray.toString());
                    userRepository.save(user);
                    for (Parent parent : user.getParents()) {
                        parent.setTaskList(user.getTaskList());
                        parentRepository.save(parent);
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //New with DTO Classes
    public String getUserTask(String data) {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            if (parentRepository.loginPassed(parentDTO.getUserName(), PasswordHash.hash(parentDTO.getPassword())) == 1) {
                Parent parent = parentRepository.getParentFromName(parentDTO.getUserName());
                User user = userRepository.getUserFromParent(parent.getUserName());
                parent.setTaskList(user.getTaskList());
                parentRepository.save(parent);
                return parent.getTaskList();
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
    public String acceptTask(String data) {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            if (parentLogin(data)) {
                Parent parent = parentRepository.getParentFromName(parentDTO.getUserName());
                User user = userRepository.getUserByName(parentDTO.getUser().getUserName());
                Level level = levelRepository.getLevelByUserName(user.getUserName());
                JSONArray userTaskList = new JSONArray(user.getTaskList());
                JSONArray taskToRemoveList = new JSONArray(new JSONObject(data).getJSONArray("taskList"));
                for (int i = 0; i < taskToRemoveList.length(); i++) {
                    String taskToRemove = taskToRemoveList.getString(i);
                    for (int j = 0; j < userTaskList.length(); j++) {
                        String userTask = userTaskList.getString(j);
                        if (taskToRemove.equals(userTask)) {
                            userTaskList.remove(j);
                            Task task = Task.valueOf(userTask);
                            level.setExp(level.getExp() + task.getExp());
                            if (level.getExp() / 1000 == 1) {
                                level.setPetLevel(level.getPetLevel() + 1);
                                level.setExp(level.getExp() % 1000);
                            }
                            break;
                        }
                    }
                }
                JSONArray userPower = new JSONArray(user.getPower());
                for (Power power : Power.values()) {
                    if (level.getPetLevel() >= power.getLevel() && !userPower.toString().contains(power.toString())) {
                        userPower.put(power.toString());
                    }
                }
                user.setPower(userPower.toString());
                user.setTaskList(userTaskList.toString());
                parent.setTaskList(user.getTaskList());
                parent.setUser(user);
                parentRepository.save(parent);
                return user.getTaskList();
            }
            return null;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //New with DTO Classes
    public String getUserPower(String data) throws NoSuchAlgorithmException {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if (userRepository.loginPassed(userDTO.getUserName(), PasswordHash.hash(userDTO.getPassword())) == 1) {
                User user = userRepository.getUserByName(userDTO.getUserName());
                return user.getPower();
            } else {
                return new JSONArray().toString();
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(Arrays.asList(Task.class.getEnumConstants()));
    }
}
