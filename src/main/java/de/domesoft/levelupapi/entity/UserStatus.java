package de.domesoft.levelupapi.entity;

public enum UserStatus {
    USER_NOT_FOUND(0),
    USER_FOUND(1);
    private final int status;
    UserStatus(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
