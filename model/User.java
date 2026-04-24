package com.techfix.model;

public class User {
    private int id;
    private String fullName;
    private String username;
    private String role;
    private String email;
    private String hireDate;

    public User(int id, String fullName, String username, String role, String email, String hireDate) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
        this.email = email;
        this.hireDate = hireDate;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getHireDate() { return hireDate; }
}