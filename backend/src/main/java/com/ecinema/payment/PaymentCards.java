package com.ecinema.payment;

import com.ecinema.services.PaymentCardsService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

    public String getDecodedCardNumber() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        PaymentCardsService service = new PaymentCardsService();
        if (this.secretKey == null) {
            return "";
        }
        SecretKey secretKey1 = this.getSecretKey();
        String decodedNumber = service.decryptCardNumber(getCardNumber(), secretKey1, this.initializationVector);

        System.out.println("This is in Payment card class j make sure not null" + decodedNumber);

        return decodedNumber;

    }

    public String getDecodedSecurityCode() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        PaymentCardsService service = new PaymentCardsService();

        if (this.secretKey == null) {
            return "";
        }
        SecretKey secretKey1 = this.getSecretKey();

        String decodedCode = service.decryptSecurityCode(getSecurityCode(), secretKey1, this.initializationVector);


        return decodedCode;


    }

}
