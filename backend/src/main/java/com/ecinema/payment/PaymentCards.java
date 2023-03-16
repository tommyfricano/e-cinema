package com.ecinema.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "paymentcards")
public class PaymentCards {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "paymentID")
    private int paymentID;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "cardnumber")
    private String cardNumber;

    @Column(name = "expirationdate")
    private String expirationDate;

    @Column(name = "securitycode")
    private String securityCode;

    @Column(name = "billingaddress")
    private String billingAddress;

    public PaymentCards(String firstName,
                        String lastName,
                        String cardNumber,
                        String expirationDate,
                        String securityCode,
                        String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.securityCode = securityCode;
        this.billingAddress = address;
    }
}
