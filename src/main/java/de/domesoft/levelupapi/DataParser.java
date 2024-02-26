package de.domesoft.levelupapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.entity.Level;
import de.domesoft.levelupapi.entity.LevelRepository;
import de.domesoft.levelupapi.entity.User;
import de.domesoft.levelupapi.entity.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DataParser {
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    UserRepository userRepository;
    @Autowired
    LevelRepository levelRepository;
    public JSONObject parseNewLevel(String data) throws Exception {
        JSONObject levelJson = new JSONObject(data);
        JSONObject userJson = levelJson.getJSONObject("user");
        User user = userRepository.getUserByName(userJson.getString("userName"));
        levelJson.remove("user");
        JSONObject newUserJson = new JSONObject(mapper.writeValueAsString(user));
        levelJson.put("user", newUserJson);
        Level level = mapper.readValue(levelJson.toString(), Level.class);
        levelRepository.save(level);
        levelJson.remove("user");
        return levelJson;
    }
    public JSONArray getLevelByName(List<Level> levelList) throws Exception{
        JSONArray returnObject = new JSONArray();
        for(Level level : levelList){
            JSONObject temp = new JSONObject(mapper.writeValueAsString(level));
            temp.remove("user");
            returnObject.put(temp);
        }
        return returnObject;

    }
}
