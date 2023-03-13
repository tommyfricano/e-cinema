package com.ecinema.users;

import com.ecinema.users.confirmation.OnRegistrationCompleteEvent;
import com.ecinema.users.confirmation.VerificationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*
Controller for all users
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

    @PostMapping("/registration")
    public void registerUserAccount(
            @RequestBody User userDto,
            HttpServletRequest request, Errors errors) {

        try {
            User registered = userService.createUser(userDto);

            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                    request.getLocale(), appUrl));
        } catch (Exception uaeEx) {
            System.out.println(uaeEx);
        }
    }

    @GetMapping("/confirmRegistration")
    public String confirmRegistration
            (WebRequest request, @RequestParam("token") String token) {

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
//    public User login(@RequestBody User user) {
//
//    }
}
