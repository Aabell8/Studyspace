package com.austinabell8.studyspace;

public class User {

    private String username;
    private String email;
    private String role;
    private String fullName;
    private String age;

    public User(String _username, String _email, String _fullName, String _age){
        username = _username;
        email = _email;
        fullName = _fullName;
        age = _age;
        role = "N";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
