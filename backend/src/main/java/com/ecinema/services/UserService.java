package com.ecinema.services;

import com.ecinema.payment.PaymentCards;
import com.ecinema.repositories.PaymentCardsRepository;
import com.ecinema.repositories.RoleRepository;
import com.ecinema.repositories.UserRespository;
import com.ecinema.users.Role;
import com.ecinema.users.User;
import com.ecinema.users.confirmation.VerificationToken;
import com.ecinema.users.confirmation.VerificationTokenRepository;
import com.ecinema.users.enums.Status;
import com.ecinema.users.enums.UserTypes;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;


/*
contains all business logic for controllers
 */
@Service
@Transactional
public class UserService {

    private final UserRespository userRespository;  //interacts with users table

    private final PaymentCardsRepository paymentCardsRepository;

    private final VerificationTokenRepository tokenRepository;  //interacts with token table

    private final RoleRepository roleRepository;

    @Autowired
    private JavaMailSender mailSender;  // used for sending confirmation emails

    @Autowired
    public UserService(UserRespository userRespository, PaymentCardsRepository paymentCardsRepository, VerificationTokenRepository tokenRepository, RoleRepository roleRepository) {   //constructor needed for dependency injection of repo
        this.userRespository = userRespository;
        this.paymentCardsRepository = paymentCardsRepository;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getUsers() {     // get a list of all user from db
        return userRespository.findAll();
    }


    public User getUser(int id) {
        return userRespository.findOneByUserID(id);
    }
    public User getUserEmail(String email) {
        return userRespository.findOneByEmail(email);
    }


    public User findUser(String email, String password) {
        User user = userRespository.findOneByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exist with this email");
        }

        if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect Password");
        }
    }


    public User createUser(User user) throws MessagingException {   // create and save a new user in db
        if(!(userRespository.findOneByEmail(user.getEmail()) == null)){  // check for duplicates
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            user.setFirstName("Registration");
            return user;
        }

        try {
            user.setUserType(UserTypes.CUSTOMER);
            Role role = roleRepository.findByName("CUSTOMER");
            user.setRoles(Arrays.asList(role));
            user.setActivity(Status.INACTIVE);  // set user status to inactive until confirmed
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            if(!(user.getPayments() == null)) {
                encryptCards(user.getPayments().get(0));
            }
            userRespository.save(user);     //save user in db
        }
        catch(Exception e){     // throw exception on failure
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating new user.");
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

    public void sendForgotPassword(String email) {
        System.out.println("thug shaker "+ email);
        User user = userRespository.findOneByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Account does not exist with this email");
        }
        String newPassword = user.getLastName() + user.getUserID();
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        SimpleMailMessage emailToUser = new SimpleMailMessage();
        emailToUser.setTo(email);
        emailToUser.setSubject("New Password");
        emailToUser.setText("Your new Password is: " + newPassword + " please update soon");
        mailSender.send(emailToUser);
    } // forgotPassword


    /*
    update user info
     */
    public void updateProfile(String username, User user){ //username is email
        User userToUpdate = userRespository.findOneByEmail(username);
        System.out.println(userToUpdate.getFirstName());
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setOptInPromo(user.isOptInPromo());
        //if (userToUpdate.getPayments() != null) {
            for (int i = 0; i < userToUpdate.getPayments().size(); i++) {
                System.out.println(userToUpdate.getPayments().get(i).getPaymentID());
                paymentCardsRepository.deleteById(userToUpdate.getPayments().get(i).getPaymentID());
            }

            // todo encrypt card info??
            encryptAllCards(user.getPayments());
            userToUpdate.setPayments(user.getPayments());
        //}
        userRespository.save(userToUpdate);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userToUpdate.getEmail());
        email.setSubject("Your account information has been updated!");     //todo update this link when connected
        email.setText("Follow this link to view changes" + "\r\n" + "http://localhost:8080/api/user/"+userToUpdate.getUserID());
        mailSender.send(email);
    }

    /*
    update user password
     */
    public void updatePassword(int id, String oldPassword, String newPassword) {
        User userToUpdate = userRespository.findOneByUserID(id);
        System.out.println(oldPassword);
        if(!(new BCryptPasswordEncoder().matches(oldPassword, userToUpdate.getPassword()))){
            throw new ResponseStatusException(BAD_REQUEST, "current password does not match");
        }
        else{
            userToUpdate.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        }
        userRespository.save(userToUpdate);
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

    public void encryptAllCards(List<PaymentCards> cards) {

        if(cards != null) {
            for (int i = 0; i < cards.size(); i++) {
                cards.get(i).setCardNumber(new BCryptPasswordEncoder().encode(cards.get(i).getCardNumber()));
                cards.get(i).setExpirationDate(new BCryptPasswordEncoder().encode(cards.get(i).getExpirationDate()));
                cards.get(i).setSecurityCode(new BCryptPasswordEncoder().encode(cards.get(i).getSecurityCode()));
            }
        }
    }

}

