package com.ecinema.users;

import com.ecinema.payment.PaymentCards;
import com.ecinema.users.confirmation.OnRegistrationCompleteEvent;
import com.ecinema.users.confirmation.VerificationToken;
import com.ecinema.users.enums.Status;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/*
Controller for users
 */

@RestController
@RequestMapping(path="/api/user")
public class UserController {
    private final UserService userService;  // business logic object
    @Autowired
    ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping  ("/")       //get all users
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUser(id);
    }

    @PostMapping("/registration")
    public void registerUserAccount(
            @RequestBody User userDto,
            HttpServletRequest request) throws MessagingException {
        try {
            User registered = userService.createUser(userDto);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                    request.getLocale(), appUrl));
        } catch (Exception uaeEx) {
            throw uaeEx;
        }
    }

    @GetMapping("/confirmRegistration")
    public String confirmRegistration
            ( @RequestParam("token") String token) {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            return "token does not exist";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "token expired";
        }
        user.setActivity(Status.ACTIVE);
        userService.confirmUser(user, verificationToken);
        return "User is now Active";
    }

    //todo login
    @PostMapping("/login")
    public int login(@RequestBody Map<String, String> json) {
        return userService.findUser(json.get("email"), json.get("password")).getUserID();
    }

    @PostMapping("/forgotPassword")
    public void ForgotPassword(@RequestBody String email) {
        userService.sendForgotPassword(email);
    }

    @GetMapping("/payments/{id}")       // gets all cards
    public List<PaymentCards> getPayments(@PathVariable int id) {
        List<PaymentCards> cards = userService.getPaymentCards(id);
        return cards;
    }

    @PatchMapping("/edit/{id}")
    public void editProfile(@PathVariable int id, @RequestBody User user){
        userService.updateProfile(id, user);
    }

    @PatchMapping("/editPassword/{id}")
    public void editPassword(@PathVariable int id, @RequestBody Map<String, String> json){
        userService.updatePassword(id, json.get("oldPassword"), json.get("newPassword"));
    }
}
