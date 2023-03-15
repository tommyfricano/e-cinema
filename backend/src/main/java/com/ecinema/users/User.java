package com.ecinema.users;

import com.ecinema.payment.PaymentCards;
import jakarta.persistence.*;
import lombok.NonNull;

import java.util.List;

@Entity
@Table(name = "users")
@NonNull
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "userID")
    private int userID;

    @Column(name = "usertype")
    private UserTypes userType;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "status")
    private Status activity;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userID", referencedColumnName = "userID", insertable = true, updatable = true)
    private List<PaymentCards> payments;
//
    public List<PaymentCards> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentCards> cards) {
        this.payments = cards;
    }

    public User() {
    }

    public User(UserTypes userType,
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

    public User(UserTypes userType,
                String firstName,
                String lastName,
                String email,
                String password,
                String phoneNumber,
                Status activity,
                List<PaymentCards> cards) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.activity = activity;
        this.payments = cards;
    }

    public int getUserID() {
        return userID;
    }

    public UserTypes getUserType() {
        return userType;
    }

    public void setUserType(UserTypes userType) {
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
