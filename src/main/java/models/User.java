/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author joonx
 */
public class User {
    private int userID;
    private String email;
    private String passwordHash;

    public User() {
    }

    public User(int userID, String email) {
        this.userID = userID;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" + "userID=" + userID + ", email=" + email + '}';
    }

    
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    
}

