package home.assignment.messageboard.model;

public class User {

    private Integer id;
    private String userName;
    private String passwordHash; // TODO: is it a string?

    public User(String userName, String passwordHash) {
        this.userName = userName;
        this.passwordHash = passwordHash;
    }

    public User(Integer id, String userName, String passwordHash) {
        this.id = id;
        this.userName = userName;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
