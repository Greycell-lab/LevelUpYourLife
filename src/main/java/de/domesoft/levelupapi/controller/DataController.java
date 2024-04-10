package de.domesoft.levelupapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.domesoft.levelupapi.dataparse.DataParser;
import de.domesoft.levelupapi.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@SuppressWarnings("unused")
@RestController
public class DataController {
    @Autowired
    DataParser dataParser;
    @PostMapping("/getLevelData")
    public String getLevelData(@RequestBody String data) {
        return dataParser.getLevel(data);
    }
    @PostMapping("/postLevelData")
    public String postLevelData(@RequestBody String data) {
        return dataParser.postLevel(data);
    }
    @GetMapping("/login")
    public boolean login(@RequestBody String data)throws NoSuchAlgorithmException {
        return dataParser.userLogin(data);
    }
    @PostMapping("/addUser")
    public boolean addUser(@RequestBody String data) {
        return dataParser.addUser(data);
    }
    @GetMapping("/getTasks")
    public List<Task> getTasks(){
        return dataParser.getTaskList();
    }
    @PostMapping("/addParent")
    public boolean addParent(@RequestBody String data)  {
        return dataParser.addParent(data);
    }
    @PostMapping("/startTask")
    public String startTask(@RequestBody String data) throws NoSuchAlgorithmException {
        return dataParser.startTask(data);
    }
    @GetMapping("/getUserTask")
    public String getUserTask(@RequestBody String data) throws NoSuchAlgorithmException{
        return dataParser.getUserTask(data);
    }
    @PostMapping("/acceptTask")
    public String acceptTask(@RequestBody String data) throws NoSuchAlgorithmException{
        return dataParser.acceptTask(data);
    }
    @PostMapping("/getUserPower")
    public String getUserPower(@RequestBody String data) throws NoSuchAlgorithmException{
        return dataParser.getUserPower(data);
    }
}
