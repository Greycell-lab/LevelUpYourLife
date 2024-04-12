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
import org.slf4j.Logger;
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
    Logger dataParserLogger;
    UserRepository userRepository;
    LevelRepository levelRepository;
    ParentRepository parentRepository;
    ObjectMapper objectMapper;

    @Autowired
    public DataParser(UserRepository userRepository,
                      LevelRepository levelRepository,
                      ParentRepository parentRepository,
                      ObjectMapper objectMapper,
                      Logger dataParserLogger) {
        this.userRepository = userRepository;
        this.levelRepository = levelRepository;
        this.parentRepository = parentRepository;
        this.objectMapper = objectMapper;
        this.dataParserLogger = dataParserLogger;
    }

    //New with DTO Classes
    public boolean postLevel(String data) {
        try {
            LevelDTO levelDTO = objectMapper.readValue(data, LevelDTO.class);
            UserDTO userDTO = new UserDTO(levelDTO.getUser().getUserName(), levelDTO.getUser().getPassword());
            if (userLogin(userDTO)) {
                createLevel(userDTO, levelDTO);
                return true;
            }
            return false;
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return false;
        }
    }

    //New with DTO Classes
    public boolean addUser(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if (!userExists(userDTO)) {
                createUser(userDTO);
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return false;
        }
    }


    //New with DTO Classes
    public String getLevel(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if (userLogin(userDTO)) {
                User user = userRepository.getUserByName(userDTO.getUserName());
                Level level = levelRepository.getLevelByUserName(userDTO.getUserName());
                String levelDTOString = createLevelDTO(level);
                setUserPower(user, level);
                return levelDTOString;
            } else {
                return null;
            }
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return null;
        }
    }

    //New with DTO Classes

    public boolean addParent(String data) {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            UserDTO userDTO = new UserDTO(parentDTO.getUser().getUserName(), parentDTO.getUser().getPassword());
            if (userLogin(userDTO) && !parentExists(parentDTO)) {
                createParent(parentDTO, userDTO);
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return false;
        }
    }
    //New with DTO Classes

    public String startTask(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if (userLogin(userDTO)) {
                User user = userRepository.getUserByName(userDTO.getUserName());
                JSONArray userTaskArray = new JSONArray(user.getTaskList());
                List<String> postedTasks = userDTO.getDoneTasks();
                if (user.getTaskList() == null) {
                    user.setTaskList(postedTasks.toString());
                }
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
                return userTaskArray.toString();
            }
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return null;
        }
        return null;
    }
    //New with DTO Classes

    public String getUserTask(String data) {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            if (parentLogin(parentDTO)) {
                Parent parent = parentRepository.getParentFromName(parentDTO.getUserName());
                User user = userRepository.getUserFromParent(parent.getUserName());
                parent.setTaskList(user.getTaskList());
                parentRepository.save(parent);
                return parent.getTaskList();
            } else {
                return null;
            }
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return null;
        }
    }
    //New with DTO Classes

    public String acceptTask(String data) {
        try {
            ParentDTO parentDTO = objectMapper.readValue(data, ParentDTO.class);
            if (parentLogin(parentDTO)) {
                Parent parent = parentRepository.getParentFromName(parentDTO.getUserName());
                User user = userRepository.getUserByName(parentDTO.getUser().getUserName());
                Level level = levelRepository.getLevelByUserName(user.getUserName());
                JSONArray taskToRemoveList = new JSONArray(new JSONObject(data).getJSONArray("taskList"));
                Level taskAcceptedLevel = taskAccepting(user, level, taskToRemoveList);
                user = taskAcceptedLevel.getUser();
                setUserPower(user, taskAcceptedLevel);
                user.setTaskList(taskAcceptedLevel.getUser().getTaskList());
                parent.setTaskList(user.getTaskList());
                parent.setUser(user);
                parentRepository.save(parent);
                return user.getTaskList();
            }
            return null;
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return null;
        }
    }
    //New with DTO Classes

    public String getUserPower(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class);
            if (userRepository.loginPassed(userDTO.getUserName(), PasswordHash.hash(userDTO.getPassword())) == 1) {
                User user = userRepository.getUserByName(userDTO.getUserName());
                return user.getPower();
            } else {
                return new JSONArray().toString();
            }
        } catch (JsonProcessingException | NoSuchAlgorithmException ex) {
            dataParserLogger.error(ex.getMessage());
            return null;
        }
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(Arrays.asList(Task.class.getEnumConstants()));
    }

    //New with DTO Classes
    public boolean userLogin(UserDTO userDTO) {
        try {
            return userRepository.loginPassed(userDTO.getUserName(), PasswordHash.hash(userDTO.getPassword())) == 1;
        } catch (NoSuchAlgorithmException ex) {
            dataParserLogger.error(ex.getMessage());
            return false;
        }
    }

    //New with DTO Classes
    public boolean parentLogin(ParentDTO parentDTO) {
        try {
            return parentRepository.loginPassed(parentDTO.getUserName(), PasswordHash.hash(parentDTO.getPassword())) == 1;
        } catch (NoSuchAlgorithmException ex) {
            dataParserLogger.error(ex.getMessage());
            return false;
        }
    }

    public boolean parentExists(ParentDTO parentDTO) {
        return parentRepository.parentExists(parentDTO.getUserName()) == 1;
    }

    public boolean userExists(UserDTO userDTO) {
        return userRepository.userExists(userDTO.getUserName()) == 1;
    }

    public void createUser(UserDTO userDTO) {
        try {
            User user = new User();
            user.setUserName(userDTO.getUserName());
            user.setPassword(PasswordHash.hash(userDTO.getPassword()));
            user.setTaskList("[]");
            user.setPower("[]");
            userRepository.save(user);
        } catch (NoSuchAlgorithmException ex) {
            dataParserLogger.error(ex.getMessage());
        }
    }

    public void createLevel(UserDTO userDTO, LevelDTO levelDTO) {
        User user = userRepository.getUserByName(userDTO.getUserName());
        Level level = new Level();
        level.setPetLevel(levelDTO.getLevel());
        level.setName(levelDTO.getName());
        level.setPet(levelDTO.getPet());
        level.setExp(levelDTO.getExp());
        level.setUser(user);
        levelRepository.save(level);
    }

    public void createParent(ParentDTO parentDTO, UserDTO userDTO) {
        try {
            User user = userRepository.getUserByName(userDTO.getUserName());
            Parent parent = new Parent();
            parent.setUser(user);
            parent.setPassword(PasswordHash.hash(parentDTO.getPassword()));
            parent.setUserName(parentDTO.getUserName());
            parent.setTaskList("[]");
            parentRepository.save(parent);
        } catch (NoSuchAlgorithmException ex) {
            dataParserLogger.error(ex.getMessage());
        }
    }

    public void setUserPower(User user, Level level) {
        user.setPower(new JSONArray().toString());
        JSONArray powerArray = new JSONArray();
        for (Power power : Power.values()) {
            if (level.getPetLevel() >= power.getLevel() && !user.getPower().contains(power.toString())) {
                powerArray.put(power.toString());
            }
        }
        user.setPower(powerArray.toString());
        userRepository.save(user);
    }

    public String createLevelDTO(Level level) {
        LevelDTO levelDTO = new LevelDTO();
        levelDTO.setLevel(level.getPetLevel());
        levelDTO.setName(level.getName());
        levelDTO.setPet(level.getPet());
        levelDTO.setExp(level.getExp());
        try {
            return objectMapper.writeValueAsString(levelDTO);
        } catch (JsonProcessingException ex) {
            dataParserLogger.error(ex.getMessage());
            return null;
        }
    }

    public Level taskAccepting(User user, Level level, JSONArray taskToRemoveList) {
        JSONArray userTaskList = new JSONArray(user.getTaskList());
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
        user.setTaskList(userTaskList.toString());
        level.setUser(user);
        return level;
    }
}
