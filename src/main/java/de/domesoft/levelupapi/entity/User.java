package de.domesoft.levelupapi.entity;

import jakarta.persistence.*;

@SuppressWarnings("unused")
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String user_name;
    private String password;
    private String taskList;
    private String power;
    public String getPower() {
        return power;
    }
    public void setPower(String power) {
        this.power = power;
    }

    public String getTasklist() {
        return taskList;
    }

    public void setTasklist(String tasklist) {
        this.taskList = tasklist;
    }

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
}
