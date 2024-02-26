package de.domesoft.levelupapi.controller;

import de.domesoft.levelupapi.DataParser;
import de.domesoft.levelupapi.entity.Level;
import de.domesoft.levelupapi.entity.LevelRepository;
import de.domesoft.levelupapi.entity.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public String setExp(@RequestBody String data) throws Exception {
        return dataParser.setExp(data).toString();
    }

}
