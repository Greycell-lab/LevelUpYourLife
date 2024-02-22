package de.domesoft.levelupapi;

public enum Task {
    VACUUM(250),
    COOKING(300);
    private final int exp;
    private Task(int exp){
        this.exp = exp;
    }
    public int getExp(){
        return exp;
    }
}
