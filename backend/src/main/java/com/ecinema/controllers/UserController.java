package com.ecinema.controllers;

import com.ecinema.models.booking.Booking;
import com.ecinema.models.movie.Movie;
import com.ecinema.models.payment.PaymentCards;
import com.ecinema.models.promotion.Promotions;
import com.ecinema.models.seat.Seat;
import com.ecinema.models.show.ShowRoom;
import com.ecinema.models.ticket.Ticket;
import com.ecinema.security.SecurityUtil;
import com.ecinema.services.*;
import com.ecinema.models.show.Show;
import com.ecinema.models.users.User;
import com.ecinema.models.users.confirmation.Utility;
import com.ecinema.services.MovieService;
import com.ecinema.services.PaymentCardsService;
import com.ecinema.services.PromotionsService;
import com.ecinema.services.ShowService;

import com.ecinema.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/*
Controller for users
 */

@Controller
public class UserController {
    private final UserService userService;  // business logic object

    private final MovieService movieService;

    private final PaymentCardsService paymentCardsService;
    private final PromotionsService promotionsService;

    private final ShowService showService;

    private final BookingService bookingService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserController(UserService userService, MovieService movieService, PromotionsService promotionsService, ShowService showService, BookingService bookingService , PaymentCardsService paymentCardsService) {
        this.userService = userService;
        this.movieService = movieService;
        this.promotionsService = promotionsService;
        this.showService = showService;
        this.paymentCardsService = paymentCardsService;
        this.bookingService = bookingService;
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
    public String getUserPage(Model model){
        Movie searchedMovie = new Movie();
        List<Movie> onMovies = movieService.getMoviesOutNow();
        List<Movie> csMovies = movieService.getMoviesComingSoon();
        model.addAttribute("searchedmovie", searchedMovie);
        model.addAttribute("onmovies", onMovies);
        model.addAttribute("csmovies", csMovies);
        return "customerHomePage";
    }

    @GetMapping("/user/descriptions/{id}")
    public String getUserDescription(@PathVariable("id")int id, Model model) throws ParseException {
        Movie movie = movieService.getMovie(id);
        Movie searchedMovie = new Movie();
        List<Show> sortedShows = showService.getSortedShows(movie.getMovieID());
        model.addAttribute("shows", sortedShows);
        model.addAttribute("searchedmovie", searchedMovie);
        model.addAttribute("movie", movie);
        return "descriptionsUser";
    }

    @GetMapping("/user/descriptions/comingSoon/{id}")
    public String getUserDescriptionComingSoon(@PathVariable("id")int id, Model model){
        Movie movie = movieService.getMovie(id);
        Movie searchedMovie = new Movie();
        model.addAttribute("searchedmovie", searchedMovie);
        model.addAttribute("movie", movie);
        return "descriptionsUserComing";
    }
    @PostMapping("/user/search")
    public String searchMovie(HttpServletResponse httpResponse, @ModelAttribute("movie")Movie movie, Model model) throws IOException {
        String redirect = "";
        if(movie.getCategory().equals("option1")) {
            Movie searchedMovie = movieService.getMovieByTitle(movie.getTitle());
            if(searchedMovie == null){
                model.addAttribute("error", true);
                return "redirect:/user/customerPage?error";
            }
            redirect = "/user/descriptions/" + searchedMovie.getMovieID();
            if (searchedMovie.getCategory().equals("Coming-Soon")) {
                redirect = "/user/descriptions/comingSoon/" + searchedMovie.getMovieID();
            }
        }
        else {
            Movie searchedMovie = new Movie();
            List<Movie> searchedGenreON = movieService.getMoviesByGenreOutNow(movie.getTitle());
            List<Movie> searchedGenreCS = movieService.getMoviesByGenreComing(movie.getTitle());
            if(searchedGenreCS.size() ==0 && searchedGenreON.size() == 0){
                model.addAttribute("error", true);
                return "redirect:/user/customerPage?error";
            }
            model.addAttribute("onmovies", searchedGenreON);
            model.addAttribute("csmovies", searchedGenreCS);
            model.addAttribute("searchedmovie", searchedMovie);
            return "customerHomePage";
        }
        httpResponse.sendRedirect(redirect);
        return null;
    }

    @GetMapping("/user/bookMovie/{id}/{sid}")
    public String getBookMovie(@PathVariable("id")int id,
                               @PathVariable("sid")int sid,
                               Model model)  {
        Movie movie = movieService.getMovie(id);
        Show show = showService.getShow(sid);
//        ShowRoom showRoom = show.getShowRoom();
        Booking booking = new Booking();
        List<Ticket> tickets = new ArrayList<>();
        List<Seat> seats = show.getShowRoom().getSeats();


        for(int i =0; i < 3 ;i++){
            System.out.println(seats.get(i).getSeatID());
            Ticket ticket = new Ticket();
            ticket.setCheck(0);
            tickets.add(ticket);
        }
        booking.setTickets(tickets);

        model.addAttribute("seats", seats);
        model.addAttribute("booking", booking);
        model.addAttribute("movie", movie);
        model.addAttribute("show", show);
        return"/descriptions/tickets/buytickets";
    }

    @PostMapping("/user/bookMovie_attempt/{id}/{sid}")
    public String bookMovie(@ModelAttribute("booking")Booking booking,
                            @PathVariable("id")int id,
                            @PathVariable("sid")int sid,
                            Model model){
        Movie movie = movieService.getMovie(id);
        Show show = showService.getShow(sid);
        String username = SecurityUtil.getSessionUser();
        User user = userService.getUserEmail(username);
        Booking newBooking = bookingService.createPartialBooking(show, booking.getTickets(), user, show.getShowRoom()); // todo
        if(newBooking == null){
            model.addAttribute("error" , true);
            return "redirect:/user/bookMovie/"+movie.getMovieID() +"/"+ show.getShowID() +"?error";
        }
        model.addAttribute("booking",  newBooking);
        return "redirect:/user/checkout";
    }


    @GetMapping("/user/checkout")
    public String getCheckout(@ModelAttribute("booking")Booking booking,
                              Model model)  {
        PaymentCards card = new PaymentCards();
        Promotions promo = new Promotions();
        //todo add users cards
        model.addAttribute("booking", booking);
        model.addAttribute("card", card);
        model.addAttribute("promo", promo);
        return"/descriptions/tickets/checkout";
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
        Movie searchedMovie = new Movie();
        model.addAttribute("searchedmovie", searchedMovie);
        model.addAttribute("user", user);
        return "Useraccount";
    }
    @GetMapping("/user/editInfo")
    public String getEditInfo(Model model) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String username = SecurityUtil.getSessionUser();
        User user = userService.getUserEmail(username);
        for(PaymentCards card: user.getPayments()){
            card.setCardNumber(card.getDecodedCardNumber().trim());
            card.setSecurityCode(card.getDecodedSecurityCode().trim());
        }
        model.addAttribute("user", user);
        return "Editprofile";
    }

    @PostMapping("/user/editInfo")
    public String editInfo(
            @Validated @ModelAttribute("user") User userDto,
            Model model) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        String username = SecurityUtil.getSessionUser();
        System.out.println("This is userDto" + userDto.getFirstName());
        List<PaymentCards> cardsToAdd = new ArrayList<>(3);
        List<PaymentCards> cardsFromPost = userDto.getPayments();
        for (int i = 0; i < 3; i++) {
            if (cardsFromPost.get(i).getCardNumber() != null && !(cardsFromPost.get(i).getCardNumber().equals(""))) {
                cardsFromPost.get(i).setLastName(userDto.getLastName());
                cardsFromPost.get(i).setBillingAddress(userDto.getAddress());
                paymentCardsService.encrypt(cardsFromPost.get(i));
                cardsToAdd.add(cardsFromPost.get(i));
            }
        }
        userDto.setPayments(cardsToAdd);
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
        String response = userService.createAdmin(user);
        httpResponse.sendRedirect(response);
    }

}

