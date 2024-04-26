package de.domesoft.levelupapi.controller;

import de.domesoft.levelupapi.dataparse.DataParser;
import de.domesoft.levelupapi.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SuppressWarnings("unused")
@RestController
public class DataController {
    private final DataParser dataParser;

    @Autowired
    public DataController(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    @GetMapping("/getLevelData")
    public ResponseEntity<String> getLevelData(@RequestBody String data) {
        return dataParser.getLevel(data);
    }

    @PostMapping("/postLevelData")
    public ResponseEntity<String> postLevelData(@RequestBody String data) {
        return dataParser.postLevel(data);
    }

    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody String data) {
        return dataParser.addUser(data);
    }

    @GetMapping("/getTasks")
    public ResponseEntity<List<Task>> getTasks() {
        return dataParser.getTaskList();
    }

    @PostMapping("/addParent")
    public ResponseEntity<String> addParent(@RequestBody String data) {
        return dataParser.addParent(data);
    }

    @PostMapping("/startTask")
    public ResponseEntity<String> startTask(@RequestBody String data) {
        return dataParser.startTask(data);
    }

    @GetMapping("/getUserTask")
    public ResponseEntity<String> getUserTask(@RequestBody String data) {
        return dataParser.getUserTask(data);
    }

    @PostMapping("/acceptTask")
    public ResponseEntity<String> acceptTask(@RequestBody String data) {
        return dataParser.acceptTask(data);
    }

    @PostMapping("/getUserPower")
    public ResponseEntity<String> getUserPower(@RequestBody String data) {
        return dataParser.getUserPower(data);
    }
}
