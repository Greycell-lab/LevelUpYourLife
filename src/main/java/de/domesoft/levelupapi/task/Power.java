package de.domesoft.levelupapi.task;

public enum Power {
    TOOTHBRUSH(2),
    AWAKELONGER(4),
    SUPERPOWER(10);
    private final int level;
    Power(int level){
        this.level = level;
    }
    public int getLevel() {
        return level;
    }
}
