package com.example.berry.ayuda_workplace.models;

public class User {
    int userID;
    String userName, userEmail;

    public User(int userID, String userEmail, String userName) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
