package com.ecinema.users;

import jakarta.persistence.*;
import lombok.NonNull;

@Entity
@Table(name = "users")
@NonNull
public class User {

    @Id
    @GeneratedValue
    private int userID;

    @Column(nullable = false)
    private int userType;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = false)
    private Status activity;

    public User() {
    }

    public User(int userID,
                int userType,
                String firstName,
                String lastName,
                String email,
                String password,
                String phoneNumber,
                Status activity) {
        this.userID = userID;
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.activity = activity;
    }

    public User(int userType,
                String firstName,
                String lastName,
                String email,
                String password,
                String phoneNumber,
                Status activity) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.activity = activity;
    }

    public int getUserID() {
        return userID;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Status getActivity() {
        return activity;
    }

    public void setActivity(Status activity) {
        this.activity = activity;
    }
}
