package com.ecinema.controllers;

import com.ecinema.payment.PaymentCards;
import com.ecinema.security.SecurityUtil;
import com.ecinema.users.User;
import com.ecinema.services.UserService;
import com.ecinema.users.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/*
Controller for users
 */

@Controller
public class UserController {
    private final UserService userService;  // business logic object
    @Autowired
    ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUser(id);
    }

    @GetMapping("/logout")
    public String logout(){
        return "Cinema";
    }
    @GetMapping("/user/customerPage")
    public String getUserPage(){
        return "customerHomePage";
    }
    @GetMapping("/admin/adminPage")
    public String getAdminPage(){
        return "AdminMainPage";
    }

    @GetMapping("/user/payments/{id}")       // gets all cards
    public List<PaymentCards> getPayments(@PathVariable int id) {
        List<PaymentCards> cards = userService.getPaymentCards(id);
        return cards;
    }

    @GetMapping("/user/account")
    public String getAccount(Model model){
        String username = SecurityUtil.getSessionUser();
        User user = userService.getUserEmail(username);
        model.addAttribute("user", user);
        return "Useraccount";
    }
    @GetMapping("/user/editInfo")
    public String getEditInfo(Model model){
        String username = SecurityUtil.getSessionUser();
        User user = userService.getUserEmail(username);
        model.addAttribute("user", user);
        return "Editprofile";
    }

    @PostMapping("/user/editInfo")
    public String editInfo(@Validated @ModelAttribute("user") User userDto){
        String username = SecurityUtil.getSessionUser();
        System.out.println("This is userDto" + userDto.getFirstName());
        userService.updateProfile(username, userDto);
        return "Useraccount";
    }




    @GetMapping("/user/editInfo/editPassword")
    public String getEditPassword(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "changepassword";
    }
    @PostMapping("/user/editInfo/editPassword")
    public String editPassword(@ModelAttribute("user")User user, HttpServletResponse httpResponse) throws IOException {
        String username = SecurityUtil.getSessionUser();
        User currentUser = userService.getUserEmail(username);
        System.out.println("here " +currentUser.getEmail());
        userService.updatePassword(currentUser.getEmail(), user.getPassword(), user.getFirstName());
        httpResponse.sendRedirect("/user/account");
        return null;
    }
}

