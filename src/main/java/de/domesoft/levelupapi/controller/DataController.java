package de.domesoft.levelupapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.PasswordHash;
import de.domesoft.levelupapi.Task;
import de.domesoft.levelupapi.entity.Level;
import de.domesoft.levelupapi.entity.LevelRepository;
import de.domesoft.levelupapi.entity.User;
import de.domesoft.levelupapi.entity.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@RestController
public class DataController {
    @Autowired
    LevelRepository levelRepository;
    @Autowired
    UserRepository userRepository;
    @GetMapping("/data")
    public List<Level> getData(){
        return levelRepository.findAll();
    }
    @PostMapping("/data")
    public Level postLevelData(@RequestBody String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject ob = new JSONObject(data);
        JSONObject userobject = ob.getJSONObject("user");
        User user = userRepository.getUserByName(userobject.getString("userName"));
        ob.remove("user");
        JSONObject uo = new JSONObject(mapper.writeValueAsString(user));
        ob.put("user", uo);
        Level level = mapper.readValue(ob.toString(), Level.class);
        levelRepository.save(level);
        return level;
    }
    @GetMapping("/login")
    public String login(@RequestBody String data)throws NoSuchAlgorithmException {
        JSONObject loginData = new JSONObject(data);
        if(PasswordHash.hash(loginData.getString("passwordHash")).equals(userRepository.getPasswordHash(loginData.getString("userName")))){
            JSONObject level = new JSONObject(levelRepository.getLevel(loginData.getString("userName")));
            level.remove("user");
            level.remove("id");
            return level.toString();
        }
        return null;
    }
    @PostMapping("/adduser")
    public void addUser(@RequestBody String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject ob = new JSONObject(data);
        try {
            ob.put("passwordHash", PasswordHash.hash(ob.getString("passwordHash")));
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        User user = mapper.readValue(ob.toString(), User.class);
        userRepository.save(user);
    }
    @PostMapping("/setexp")
    public Level setExp(@RequestBody String data) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject ob = new JSONObject(data);
        Task task = ob.getEnum(Task.class, "task");
        ob.remove("task");
        System.out.println(ob.getJSONObject("user").toString());
        ob.getJSONObject("user").put("passwordHash", PasswordHash.hash(ob.getJSONObject("user").getString("passwordHash")));
        Level level = levelRepository.getLevel(ob.getJSONObject("user").getString("userName"));
        level.setExp(level.getExp() + task.getExp());
        levelRepository.save(level);
        return level;
    }

}
