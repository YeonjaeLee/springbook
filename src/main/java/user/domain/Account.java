package user.domain;

public class Account {
    String id;
    int cash;

    public Account(String id, int cash){
        this.id = id;
        this.cash = cash;
    }

    public Account(){ }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }
}
