package com.ecinema.controllers;

import com.ecinema.models.businesslogic.*;
import com.ecinema.models.movie.Movie;
import com.ecinema.models.promotion.Promotions;
import com.ecinema.models.show.Show;
import com.ecinema.models.users.User;
import com.ecinema.models.users.confirmation.Utility;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/*
Controller for admins
 */

@Controller
@SessionAttributes("seating")
public class AdminController {

    private final UserService userService;  // business logic object

    private final MovieService movieService;

    private final PaymentCardsService paymentCardsService;
    private final PromotionsService promotionsService;

    private final ShowService showService;

    private final BookingService bookingService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    public AdminController(UserService userService, MovieService movieService, PromotionsService promotionsService, ShowService showService, BookingService bookingService , PaymentCardsService paymentCardsService) {
        this.userService = userService;
        this.movieService = movieService;
        this.promotionsService = promotionsService;
        this.showService = showService;
        this.paymentCardsService = paymentCardsService;
        this.bookingService = bookingService;
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
        Show show = new Show();
        Movie movie = new Movie();
        List<Movie> movies = movieService.getMovies();
        model.addAttribute("movieVal", movie);
        model.addAttribute("show",show);
        model.addAttribute("movies", movies);
        return "scheduleMovies";
    }

    @PostMapping("/admin/scheduleMovie_attempt")
    public String scheduleMovie(@ModelAttribute("show")Show show, @ModelAttribute("movieVal")Movie movie, HttpServletResponse httpResponse, Model model) throws IOException, ParseException {
        show.setMovie(movieService.getMovie(movie.getMovieID()));
        String response = showService.createShowTime(show);
        if(response.contains("error")) {
            model.addAttribute("error", true);
        }
        model.addAttribute("scheduleSuccess", true);

        return response;
    }

    @GetMapping("/admin/promotions")
    public String getPromotions(Model model){
        List<Promotions> promotions = promotionsService.getPromotions();
        promotions.removeIf(promos -> promos.getCode().equals("No promo"));
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

    @PostMapping("/admin/sendPromotion/{id}")
    public String sendPromotion(@PathVariable("id")int id, HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse httpResponse, Model model) throws IOException, MessagingException {
        String link = Utility.getSiteURL(request) + "/login";
        String response = promotionsService.sendPromotion(id, link);
        if(response.equals("error")){
            response = "redirect:/admin/promotions?error";
        }
        redirectAttributes.addAttribute("emailSuccess",true);
//        httpResponse.sendRedirect(response);
        return response;
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

    @PostMapping("/admin/suspendUser/{id}")
    public void suspendUser(@PathVariable("id") int id,
                           HttpServletResponse httpResponse,
                           Model model) throws IOException {
        userService.suspendUser(id);
        httpResponse.sendRedirect("/admin/users");
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
//        String response = userService.createAdmin(user);
        User response = UserFactory.create("admin", user);
        if(response == null ){
            httpResponse.sendRedirect( "/admin/users?error");
        }
        else{
            httpResponse.sendRedirect("/admin/users?success");
        }
    }

}

