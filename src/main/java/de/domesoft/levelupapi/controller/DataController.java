package de.domesoft.levelupapi.controller;

import de.domesoft.levelupapi.dataparse.DataParser;
import de.domesoft.levelupapi.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SuppressWarnings("unused")
@RestController
public class DataController {
    @Autowired
    DataParser dataParser;
    @PostMapping("/getLevelData")
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
    @PostMapping("/addUser")
    public boolean addUser(@RequestBody String data) throws Exception {
        return dataParser.postNewUser(data);
    }
    @PostMapping("/setExp")
    public String setExp(@RequestBody String data) throws Exception {
        return dataParser.setExp(data).toString();
    }
    @GetMapping("/getTasks")
    public List<Task> getTasks(){
        return dataParser.getTaskList();
    }
    @PostMapping("/addParent")
    public boolean addParent(@RequestBody String data) throws Exception {
        return dataParser.postNewParent(data);
    }
}
