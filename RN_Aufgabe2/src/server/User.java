package server;

public class User {

    private String name;
    private String host;

    public User(String name, String host){
        this.name = name;
        this.host = host;
    }

    public String getName(){
        return this.name;
    }

    public String getHost(){
        return this.host;
    }

    public String toString(){
        return this.host + " " + this.name;
    }
}
