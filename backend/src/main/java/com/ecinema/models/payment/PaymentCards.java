package com.ecinema.models.payment;

import com.ecinema.models.booking.Booking;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.crypto.SecretKey;

import java.util.List;

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

    @Column(name= "cardholdername")
    private String cardholderName;

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

    private SecretKey secretKey;

    private byte[] initializationVector;





    @OneToMany(mappedBy = "paymentCards")
    private List<Booking> bookings;

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
