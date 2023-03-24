package com.ecinema.users;

import com.ecinema.payment.PaymentCards;
import com.ecinema.users.enums.OptInPromo;
import com.ecinema.users.enums.Status;
import com.ecinema.users.enums.UserTypes;
import jakarta.persistence.*;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NonNull
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @Column(name = "optinpromo")
    private boolean optInPromo;

    @Column(name = "address")
    private String Address;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userID", referencedColumnName = "userID", insertable = true, updatable = true, nullable = true)
    private List<PaymentCards> payments;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName ="userID" )},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "roleID")}
    )
    private List<Role> roles = new ArrayList<Role>();


    public User() {
    }

    public User(UserTypes userType,
                String firstName,
                String lastName,
                String email,
                String password,
                String phoneNumber,
                Status activity,
                List<PaymentCards> cards,
                boolean optInPromo) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.activity = activity;
        this.payments = cards;
        this.optInPromo = optInPromo;
    }

    public boolean isOptInPromo() {
        return optInPromo;
    }

    public void setOptInPromo(boolean optInPromo) {
        this.optInPromo = optInPromo;
    }
}
