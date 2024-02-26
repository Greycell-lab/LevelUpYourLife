package de.domesoft.levelupapi;

public enum Task {
    VACUUM(250),
    COOKING(300);
    private final long exp;
    private Task(int exp){
        this.exp = exp;
    }
    public long getExp(){
        return exp;
    }
}
