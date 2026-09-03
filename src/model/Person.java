package model;
import interfaces.Persistable;
public abstract class Person implements Persistable {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    public Person(String id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getMemberId() {
        return id;
    }
    public void setMemberId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
}
