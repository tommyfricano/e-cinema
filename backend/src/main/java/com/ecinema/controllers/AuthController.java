package com.ecinema.controllers;

import com.ecinema.models.payment.PaymentCards;
import com.ecinema.models.businesslogic.PaymentCardsService;
import com.ecinema.models.businesslogic.UserFactory;
import com.ecinema.models.businesslogic.UserService;
import com.ecinema.models.users.User;
import com.ecinema.models.users.confirmation.OnRegistrationCompleteEvent;
import com.ecinema.models.users.confirmation.Utility;
import com.ecinema.models.users.confirmation.VerificationToken;
import com.ecinema.models.users.enums.Status;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
public class AuthController {
    private final UserService userService;

    private final PaymentCardsService paymentCardsService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    public AuthController(UserService userService, PaymentCardsService paymentCardsService) {
        this.userService = userService;
        this.paymentCardsService = paymentCardsService;
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
    public String forgotPassword(Model model){
        return "ForgotPassword";
    }

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
    public String registrationError(@ModelAttribute("user") User user, Model model) {
        PaymentCards payment = new PaymentCards();
        model.addAttribute("registrationError", true);
        model.addAttribute("user", user);
        model.addAttribute("payment", payment);
        return "redirect:/registration?error";
    }

    @PostMapping("/registration_attempt")
    public String registerUserAccount(
            @Validated @ModelAttribute("user") User userDto, @ModelAttribute("payment") PaymentCards payment,
            HttpServletRequest request, Model model) throws MessagingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try {
            List<PaymentCards> card = new ArrayList<>(); //string equals ""
            //if (payment.getCardNumber().)
            if(payment.getCardNumber() != null && !(payment.getCardNumber().equals(""))) {
                System.out.println("This is the card number" + payment.getCardNumber());
                payment.setFirstName(userDto.getFirstName());
                payment.setLastName(userDto.getLastName());
                payment.setBillingAddress(userDto.getAddress());
                paymentCardsService.encrypt(payment);
                card.add(payment);
                userDto.setPayments(card);
            }

            System.out.println("user payments here" + userDto.getPayments());
            //System.out.println("card one" + userDto.getPayments().get(0).getCardNumber());
//            User registered = userService.createUser(userDto);
            User registered = UserFactory.create("customer", userDto);
            System.out.println("user created");
            if(registered.getPassword().equals("error")){
                return "redirect:/registration-error";
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
            (@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addAttribute("verified",true);
        userService.confirmUser(user, verificationToken);
        return "redirect:/login";
    }

    static String usingRandomUUID() {

        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");

    }
}

