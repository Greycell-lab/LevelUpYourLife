package de.domesoft.levelupapi.dto;

public class UserDTO {
    private Long id;
    private String user_name;
    private String password;
    private String taskList;
    private String power;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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
