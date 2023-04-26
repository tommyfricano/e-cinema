package com.ecinema.services;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.Base64;

import com.ecinema.models.payment.PaymentCards;
import com.ecinema.repositories.BookingRepository;
import com.ecinema.repositories.PaymentCardsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class PaymentCardsService {

    private final PaymentCardsRepository paymentCardsRepository;
    private final BookingRepository bookingRepository;



    @Autowired
    public PaymentCardsService(PaymentCardsRepository paymentCardsRepository, BookingRepository bookingRepository) {
        this.paymentCardsRepository = paymentCardsRepository;
        this.bookingRepository = bookingRepository;
    }




    public void encrypt(PaymentCards card) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        String number = card.getCardNumber();
        String securityCode = card.getSecurityCode();


        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        card.setInitializationVector(iv);
        card.setSecretKey(secretKey);


        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] numberByte = cipher.doFinal(padString(number));
        byte[] securityCodeByte = cipher.doFinal(padString(securityCode));


        byte[] encodedNumber = Base64.getEncoder().encode(numberByte);
        byte[] encodedCode = Base64.getEncoder().encode(securityCodeByte);


        card.setCardNumber(new String(encodedNumber));
        card.setSecurityCode(new String(encodedCode));
        paymentCardsRepository.save(card);

    }

    public String decryptCardNumber(String cardNumber, SecretKey secretKey, byte[] iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        String decryptedCardNumberString = new String(decryptCipher.doFinal(Base64.getDecoder().decode(cardNumber)));

        return decryptedCardNumberString;
    }

    public String decryptSecurityCode(String securityCode, SecretKey secretKey, byte[] iv) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        String decryptedCode = new String(decryptCipher.doFinal(Base64.getDecoder().decode(securityCode)));

        return decryptedCode;
    }

    public static byte[] padString(String input) {
        int blocksize = 16;
        byte padding = (byte) (blocksize - (input.length() & blocksize));
        byte[] paddedInput = new byte[input.length() + padding];
        System.arraycopy(input.getBytes(), 0, paddedInput, 0, input.length());
        for (int i = input.length(); i < paddedInput.length; i++){
            paddedInput[i] = padding;
        }
        return paddedInput;

    }

    public void setDecodedCardNumber(PaymentCards card) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        //PaymentCardsService service = new PaymentCardsService(paymentCardsRepository);
        if (card.getSecretKey() == null) {
            //return "";
        }
        SecretKey secretKey1 = card.getSecretKey();
        String decodedNumber = this.decryptCardNumber(card.getCardNumber(), secretKey1, card.getInitializationVector());

        card.setCardNumber(decodedNumber.trim());
        //return decodedNumber;

    }

    public void setDecodedSecurityCode(PaymentCards card) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        //PaymentCardsService service = new PaymentCardsService(paymentCardsRepository);

        if (card.getSecretKey() == null) {
            //return "";
        }
        SecretKey secretKey1 = card.getSecretKey();

        String decodedCode = this.decryptSecurityCode(card.getSecurityCode(), secretKey1, card.getInitializationVector());


        card.setSecurityCode(decodedCode.trim());
        //return decodedCode;


    }

    public void remove(PaymentCards cards) {
        paymentCardsRepository.delete(cards);
    }

    public boolean equals(PaymentCards cardOne, PaymentCards cardTwo) {
        if (cardOne.getCardNumber().trim().equals(cardTwo.getCardNumber().trim())
                && cardOne.getExpirationDate().equals(cardTwo.getExpirationDate())) {
            return true;
        }

        return false;
    }


}


