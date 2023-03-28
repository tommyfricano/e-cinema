package com.ecinema.controllers;

import com.ecinema.movie.Movie;
import com.ecinema.payment.PaymentCards;
import com.ecinema.promotion.Promotions;
import com.ecinema.security.SecurityUtil;
import com.ecinema.services.MovieService;
import com.ecinema.services.PromotionsService;
import com.ecinema.users.User;
import com.ecinema.services.UserService;
import com.ecinema.users.confirmation.Utility;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/*
Controller for users
 */

@Controller
public class UserController {
    private final UserService userService;  // business logic object

    private final MovieService movieService;

    private final PromotionsService promotionsService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService, MovieService movieService, PromotionsService promotionsService) {
        this.userService = userService;
        this.movieService = movieService;
        this.promotionsService = promotionsService;
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


    /*
    admin controllers
     */

    @GetMapping("/admin/adminPage")
    public String getAdminPage(Model model){
        List<Movie> onMovies = movieService.getMoviesOutNow();
        List<Movie> csMovies = movieService.getMoviesComingSoon();
        model.addAttribute("onmovies", onMovies);
        model.addAttribute("csmovies", csMovies);
        return "AdminMainPage";
    }

    @GetMapping("/admin/descriptions/{id}")
    public String getDescription(@PathVariable("id")int id, Model model){
        Movie movie = movieService.getMovie(id);
        model.addAttribute("movie", movie);
        return "descriptions";
    }
    @GetMapping("/admin/manageMovies")
    public String getManageMovies(Model model){
//        List<Movie> movies = movieService.getMovies();
        List<Movie> outNow = movieService.getMoviesOutNow();
        List<Movie> comingSoon = movieService.getMoviesComingSoon();
        model.addAttribute("outNow", outNow);
        model.addAttribute("ComingSoon", comingSoon);

        return "managemovies";
    }

    @GetMapping("/admin/addMovie")
    public String getAddMovie(Model model){
        Movie movie = new Movie();
        model.addAttribute("movie", movie);
        return "addmovies";
    }

    @GetMapping("/admin/addMovie-error")
    public String getAddMovieError(Model model){
        model.addAttribute("error", true);
        return "addmovies";
    }

    @PostMapping("/admin/addMovie")
    public String addMovie(@ModelAttribute("movie")Movie movie, HttpServletResponse httpResponse, Model model) throws IOException {
        String response = movieService.saveMovie(movie);
        if(response.equals("error")){
            response = "/admin/addMovie?error";
        }
        httpResponse.sendRedirect(response);
        return null;
    }

    @GetMapping("/admin/editMovie/{id}")
    public String getEditMovie(@PathVariable("id") int id, Model model){
        Movie movie = movieService.getMovie(id);
        model.addAttribute("movie", movie);
        return "editmovie";
    }

    @GetMapping("/admin/editMovie-error")
    public String getEditMovieError(Model model){
        model.addAttribute("error", true);
        return "editmovie";
    }

    @PostMapping("/admin/editMovie/{id}")
    public String editMovie(@PathVariable("id") int id,
                            @ModelAttribute("movie")Movie movie,
                            HttpServletResponse httpResponse,
                            Model model) throws IOException {
        String response = movieService.editMovie(id, movie);
        if(response.equals("error")){
            response = "/admin/editMovie?error";
        }
        httpResponse.sendRedirect(response);
        return null;
    }

    @PostMapping("/admin/deleteMovie/{id}")
    public void deleteMovie(@PathVariable("id") int id,
                            HttpServletResponse httpResponse,
                            Model model) throws IOException {
        movieService.deleteMovie(id);
        httpResponse.sendRedirect("/admin/manageMovies");
    }

    @GetMapping("/admin/scheduleMovie")
    public String getScheduleMovie(Model model){
        List<Movie> movies = movieService.getMovies();
        model.addAttribute("movies", movies);
        return "scheduleMovies";
    }

    @PostMapping("/admin/scheduleMovie/{id}")
    public void scheduleMovie(@PathVariable("id") int id, HttpServletResponse httpResponse, Model model) throws IOException {

        httpResponse.sendRedirect("/admin/manageMovies");
    }

    @GetMapping("/admin/promotions")
    public String getPromotions(Model model){
        List<Promotions> promotions = promotionsService.getPromotions();
        Promotions promo = new Promotions();
        model.addAttribute("promotions", promotions);
        model.addAttribute("promo", promo);
        return "promotions";
    }

    @GetMapping("/admin/editPromotion/{id}")
    public String editPromotion(@PathVariable("id")int id, Model model){
        Promotions promo = promotionsService.getPromotion(id);
        model.addAttribute("promo", promo);
        return "EditPromotion";
    }

    @PostMapping("/admin/promotions")
    public String addPromotion(@ModelAttribute("promotion")Promotions promo, HttpServletRequest request, HttpServletResponse httpResponse, Model model) throws IOException, MessagingException {
        String link = Utility.getSiteURL(request) + "/login";
        String response = promotionsService.savePromotion(promo, link);
        if(response.equals("error")){
            response = "/admin/promotions?error";
        }
        httpResponse.sendRedirect(response);
        return null;
    }

    @PostMapping("/admin/editPromotion/{id}")
    public String editMovie(@PathVariable("id") int id,
                            @ModelAttribute("promotion")Promotions promotion,
                            HttpServletResponse httpResponse,
                            Model model) throws IOException {
        String response = promotionsService.editPromo(id, promotion);
        if(response.equals("error")){
            response = "/admin/promotions?error";
        }
        httpResponse.sendRedirect(response);
        return null;
    }

    @PostMapping("/admin/deletePromotion/{id}")
    public void deletePromotion(@PathVariable("id") int id,
                            HttpServletResponse httpResponse,
                            Model model) throws IOException {
        promotionsService.deletePromo(id);
        httpResponse.sendRedirect("/admin/promotions");
    }

    @GetMapping("/admin/users")
    public String getUsers(Model model){
        List<User> users = userService.getUsers();
        model.addAttribute("users", users);
        return "userpage";
    }

    @GetMapping("/admin/editUser/{id}")
    public String getEditUser(@PathVariable("id") int id, Model model){
        User user = userService.getUser(id);
        model.addAttribute("user", user);
        return "EditUserPage";
    }

    @PostMapping("/admin/editUser/{id}")
    public String editUser(@PathVariable("id") int id,
                            @ModelAttribute("user")User user,
                            HttpServletResponse httpResponse,
                            Model model) throws IOException {
        String response = userService.userUpdateByAdmin(id, user);
        if(response.equals("error")){
            response = "/admin/users?error";
        }
        httpResponse.sendRedirect(response);
        return null;
    }

    @PostMapping("/admin/deleteUser/{id}")
    public void deleteUser(@PathVariable("id") int id,
                                HttpServletResponse httpResponse,
                                Model model) throws IOException {
        userService.deleteUser(id);
        httpResponse.sendRedirect("/admin/users");
    }

    @GetMapping("/admin/addAdmin")
    public String getAddAdmin(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "addAdmin";
    }

    @PostMapping("/admin/addAdmin_attempt")
    public void createAdmin(@ModelAttribute("user")User user,
                              HttpServletResponse httpResponse,
                              Model model) throws IOException {
        String response = userService.createAdmin(user);
        httpResponse.sendRedirect(response);
    }

}

