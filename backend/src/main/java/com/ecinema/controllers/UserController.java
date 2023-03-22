package com.ecinema.controllers;

import com.ecinema.payment.PaymentCards;
import com.ecinema.users.User;
import com.ecinema.services.UserService;
import com.ecinema.users.confirmation.OnRegistrationCompleteEvent;
import com.ecinema.users.confirmation.VerificationToken;
import com.ecinema.users.enums.Status;
import com.ecinema.users.enums.UserTypes;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/*
Controller for users
 */

@Controller
//@RequestMapping(path="/api/user")
public class UserController {
    private final UserService userService;  // business logic object
    @Autowired
    ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping  ("/")       //get all users
//    public String getUsers(Model model){
//        //todo return movies?
//        model.addAttribute("mainpage");
//        return "Cinema";
//    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUser(id);
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("user") User user) {
        User cUser = userService.findUser(user.getEmail(),user.getPassword());
        if(cUser.getUserType() == UserTypes.ADMIN){
            return"redirect:/AdminMainPage";
        }
        else {
            return "redirect:/customerHomePage";
        }
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
