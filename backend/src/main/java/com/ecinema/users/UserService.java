package com.ecinema.users;

import com.ecinema.payment.PaymentCards;
import com.ecinema.users.confirmation.VerificationToken;
import com.ecinema.users.confirmation.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;


/*
contains all business logic for controllers
 */
@Service
public class UserService {

    private final UserRespository userRespository;  //interacts with users table

    private final VerificationTokenRepository tokenRepository;  //interacts with token table

    @Autowired
    private JavaMailSender mailSender;  // used for sending confirmation emails

    @Autowired
    public UserService(UserRespository userRespository, VerificationTokenRepository tokenRepository) {   //constructor needed for dependency injection of repo
        this.userRespository = userRespository;
        this.tokenRepository = tokenRepository;
    }

    public List<User> getUsers() {     // get a list of all user from db
        return userRespository.findAll();
    }

    public User createUser(User user) throws MessagingException {   // create and save a new user in db
        if(!(userRespository.findOneByEmail(user.getEmail()) == null)){  // check for duplicates
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        try {
            user.setUserType(UserTypes.CUSTOMER);
            user.setActivity(Status.INACTIVE);  // set user status to inactive until confirmed
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            encryptCards(user.getPayments().get(0));
            userRespository.save(user);     //save user in db
        }
        catch(Exception e){     // throw exception on failure
            throw e;
        }
        return user;
    }


    /*
    all user activation
     */
    public void confirmUser(User user, VerificationToken token) {     // confirm user-> change status to active
        userRespository.save(user);
        tokenRepository.deleteById(token.getId());
    }
    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }


    /*
    get users cards
     */
    public List<PaymentCards> getPaymentCards(int userId) {
        User user = userRespository.findOneByUserID(userId);
        if(user == null){
            throw new ResponseStatusException(NOT_FOUND, "Invalid user id");
        }
        List<PaymentCards> cards = user.getPayments();

        return cards;
    }

    /*
    encrypt card information
     */
    public void encryptCards(PaymentCards card) {
            card.setCardNumber(new BCryptPasswordEncoder().encode(card.getCardNumber()));
            card.setExpirationDate(new BCryptPasswordEncoder().encode(card.getExpirationDate()));
            card.setSecurityCode(new BCryptPasswordEncoder().encode(card.getSecurityCode()));
    }

}

