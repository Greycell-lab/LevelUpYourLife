package de.domesoft.levelupapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.DataParser;
import de.domesoft.levelupapi.PasswordHash;
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

@SuppressWarnings("unused")
@RestController
public class DataController {
    @Autowired
    LevelRepository levelRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DataParser dataParser;
    @GetMapping("/getLevelData")
    public String getLevelData(@RequestBody String data) throws Exception {
        return dataParser.getLevelsByUser(data).toString();
    }
    @PostMapping("/postLevelData")
    public String postLevelData(@RequestBody String data) throws Exception {
        return dataParser.postNewLevel(data).toString();
    }
    @GetMapping("/login")
    public boolean login(@RequestBody String data)throws Exception {
        return dataParser.login(data);
    }
    @PostMapping("/adduser")
    public boolean addUser(@RequestBody String data) throws Exception {
        return dataParser.postNewUser(data);
    }
    @PostMapping("/setexp")
    public Level setExp(@RequestBody String data) throws Exception {
        /*ObjectMapper mapper = new ObjectMapper();
        JSONObject ob = new JSONObject(data);
        Task task = ob.getEnum(Task.class, "task");
        ob.remove("task");
        System.out.println(ob.getJSONObject("user").toString());
        ob.getJSONObject("user").put("passwordHash", PasswordHash.hash(ob.getJSONObject("user").getString("passwordHash")));
        List<Level> levelList = levelRepository.getLevel(ob.getJSONObject("user").getString("userName"));
        Level level = null;
        for(Level l : levelList){
            if(l.getName().equals(ob.getString("name"))){
                level = l;
            }
        }
        level.setExp(level.getExp() + task.getExp());
        levelRepository.save(level);
        return level;*/
        return null;
    }

}
