package com.ecinema.services;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.Base64;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecinema.payment.PaymentCards;

@Service
@Transactional
public class PaymentCardsService {



    @Autowired
    public PaymentCardsService() {
    }




    public void encrypt(PaymentCards card) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        String number = card.getCardNumber();
        String securityCode = card.getSecurityCode();

        System.out.println("Card number before encryption befings" + number);

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

    }

    public String decryptCardNumber(String cardNumber, SecretKey secretKey, byte[] iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        String decryptedCardNumberString = new String(decryptCipher.doFinal(Base64.getDecoder().decode(cardNumber)));
        System.out.println("Card number after decode in decrypted" + decryptedCardNumberString);

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
}

