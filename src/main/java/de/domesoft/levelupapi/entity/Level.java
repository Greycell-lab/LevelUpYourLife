package de.domesoft.levelupapi.entity;

import jakarta.persistence.*;
@SuppressWarnings("unused")
@Entity
@Table(name = "level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Long petLevel;
    private Long exp;
    private String pet;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    public String getPet() {
        return pet;
    }
    public void setPet(String pet) {
        this.pet = pet;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPetLevel() {
        return petLevel;
    }

    public void setPetLevel(Long petLevel) {
        this.petLevel = petLevel;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }
}
