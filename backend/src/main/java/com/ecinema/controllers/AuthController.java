package com.ecinema.controllers;

import com.ecinema.payment.PaymentCards;
import com.ecinema.services.UserService;
import com.ecinema.users.User;
import com.ecinema.users.confirmation.OnRegistrationCompleteEvent;
import com.ecinema.users.confirmation.Utility;
import com.ecinema.users.confirmation.VerificationToken;
import com.ecinema.users.enums.Status;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import java.util.*;

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
        return "ForgotPassword";
    }
//    @PostMapping("/forgotPassword")
//    public String ForgotPassword(@ModelAttribute("user") User user) {
//        System.out.println(user.getEmail());
//        userService.sendForgotPassword(user.getEmail());
//        return"CustomerLogin";
//    }

    @PostMapping("/forgotPassword")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = usingRandomUUID();

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/resetPassword?token=" + token;
            userService.sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "ForgotPassword";
    }

    @GetMapping("/resetPassword")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (customer == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        }

        return "userResetPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        System.out.println("token "+ token + " pass "+ password);
        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (customer == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        } else {
            userService.updatePassword(customer, password);

            model.addAttribute("message", "You have successfully changed your password.");
        }

        return "redirect:/login";

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
            if(payment.getCardNumber() != null || !(payment.getCardNumber().equals(""))) {
                payment.setFirstName(userDto.getFirstName());
                payment.setLastName(userDto.getLastName());
                payment.setBillingAddress(userDto.getAddress());
                card.add(payment);
                userDto.setPayments(card);
            }
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
        return "redirect:/login?success";
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

    static String usingRandomUUID() {

        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");

    }
}

