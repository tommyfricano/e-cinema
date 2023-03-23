package com.ecinema.controllers;

import com.ecinema.payment.PaymentCards;
import com.ecinema.services.UserService;
import com.ecinema.users.User;
import com.ecinema.users.confirmation.OnRegistrationCompleteEvent;
import com.ecinema.users.confirmation.VerificationToken;
import com.ecinema.users.enums.Status;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class AuthController {
    private final UserService userService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage( Model model) {
        return "Customerlogin";
    }

    @RequestMapping("/login-error.html")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "Customerlogin";
    }

    @GetMapping("/forgotPassword")
    public String getForgotPassword(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "ForgotPassword";
    }
    @PostMapping("/forgotPassword")
    public String ForgotPassword(@ModelAttribute("user") User user) {
        System.out.println(user.getEmail());
        userService.sendForgotPassword(user.getEmail());
        return"CustomerLogin";
    }

    @GetMapping("/registration")
    public String getRegisterPage(Model model) {
        User user = new User();
        PaymentCards payment = new PaymentCards();
        model.addAttribute("user", user);
        model.addAttribute("payment", payment);
        return "Registration";
    }

    @RequestMapping("/registration-error")
    public String registrationError(Model model) {
        model.addAttribute("registrationError", true);
        return "Registration";
    }

    @PostMapping("/registration_attempt")
    public String registerUserAccount(
            @Validated @ModelAttribute("user") User userDto, @ModelAttribute("payment") PaymentCards payment,
            HttpServletRequest request, Model model) throws MessagingException {
        try {
            List<PaymentCards> card = new ArrayList<>();
            if(payment.getCardNumber() != null || payment.getCardNumber().equals("")) {
                payment.setFirstName(userDto.getFirstName());
                payment.setLastName(userDto.getLastName());
                card.add(payment);
            }
            userDto.setPayments(card);
            User registered = userService.createUser(userDto);
            if(registered.getFirstName().equals("Registration")){
                return "Registration-error";
            }
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                    request.getLocale(), appUrl));
        } catch (Exception uaeEx) {
            throw uaeEx;
        }
        return "redirect:/login";
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
}
