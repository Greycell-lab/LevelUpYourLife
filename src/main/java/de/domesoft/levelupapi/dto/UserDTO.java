package de.domesoft.levelupapi.dto;

import java.util.List;

@SuppressWarnings("unused")
public class UserDTO {
    private Long id;
    private String userName;
    private String password;
    private String taskList;
    private String power;
    private List<String> doneTasks;

    public UserDTO() {
    }

    public UserDTO(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public List<String> getDoneTasks() {
        return doneTasks;
    }

    public void setDoneTasks(List<String> doneTasks) {
        this.doneTasks = doneTasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTaskList() {
        return taskList;
    }

    public void setTaskList(String taskList) {
        this.taskList = taskList;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }
}
