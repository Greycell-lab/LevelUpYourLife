package de.domesoft.levelupapi.dto;

import java.util.List;

@SuppressWarnings("unused")
public class UserTaskDTO {
    private String userName;
    private String password;
    private List<String> doneTasks;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
