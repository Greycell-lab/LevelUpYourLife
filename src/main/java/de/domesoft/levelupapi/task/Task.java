package de.domesoft.levelupapi.task;
@SuppressWarnings("unused")
public enum Task {
    VACUUM(250),
    COOKING(300),
    CLEANUP(250),
    WASHUP(300);
    private final long exp;
    Task(int exp){
        this.exp = exp;
    }
    public long getExp(){
        return exp;
    }
}
