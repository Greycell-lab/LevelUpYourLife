package de.domesoft.levelupapi.dto;

import org.json.JSONArray;

import java.util.List;

public class UserTaskDTO {
    private String username;
    private String password;
    private List<String> doneTasks;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getDoneTasks() {
        return doneTasks;
    }

    public void setDoneTasks(List<String> doneTasks) {
        this.doneTasks = doneTasks;
    }
}
