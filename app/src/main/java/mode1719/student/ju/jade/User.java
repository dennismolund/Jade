package mode1719.student.ju.jade;

public class User {
    private String uName;
    private String uID;

    public User(){}

    public User(String name, String ID){
        uName = name;
        uID = ID;
    }

    public String getuName() { return uName; }

    public void setuName(String uName) { this.uName = uName; }

    public String getuID() { return uID; }

    public void setuID(String uID) { this.uID = uID; }
}