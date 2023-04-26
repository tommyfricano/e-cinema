package com.ecinema.controllers;

import com.ecinema.models.movie.Movie;
import com.ecinema.models.show.Show;
import com.ecinema.models.businesslogic.services.MovieService;
import com.ecinema.models.businesslogic.services.ShowService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class Webpage {

    private final MovieService movieService;

    private final ShowService showService;

    @Autowired
    public Webpage(MovieService movieService, ShowService showService) {
        this.movieService = movieService;
        this.showService = showService;
    }

    @GetMapping("/Cinema.html")
    public String getMainPage(Model model) {

        List<Movie> onMovies = movieService.getMoviesOutNow();
        List<Movie> csMovies = movieService.getMoviesComingSoon();
        List<Movie> topMovies = movieService.getTopMovies();
        Movie mainMovie = new Movie();
        if(topMovies.size() != 0) {
            mainMovie = topMovies.get(0);
            topMovies.remove(0);
        }
        Movie searchedMovie = new Movie();
        model.addAttribute("searchedmovie", searchedMovie);
        model.addAttribute("mainmovie",mainMovie);
        model.addAttribute("topmovies",topMovies);
        model.addAttribute("onmovies", onMovies);
        model.addAttribute("csmovies", csMovies);
        return "Cinema";
    }

    @GetMapping("/descriptions/{id}")
    public String getDescription(@PathVariable("id")int id, Model model) throws ParseException {
        Movie movie = movieService.getMovie(id);
        Movie searchedMovie = new Movie();
        List<Show> sortedShows = showService.getSortedShows(movie.getMovieID());
        model.addAttribute("shows", sortedShows);
        model.addAttribute("searchedmovie", searchedMovie);
        model.addAttribute("movie", movie);
        return "descriptionsNoAuth";
    }

    @GetMapping("/bookMovie/{id}/{sid}")
    public String getBookMovie(@PathVariable("id")int id, @PathVariable("sid")int sid, Model model) throws ParseException {
        Movie movie = movieService.getMovie(id);
        Show show = showService.getShow(sid);
        model.addAttribute("movie", movie);
        model.addAttribute("show", show);
        return"/descriptions/tickets/buyticketsNoAuth";
    }

    @PostMapping("/search")
    public String searchMovieNoAuth(HttpServletResponse httpResponse, @ModelAttribute("movie")Movie movie, Model model) throws IOException {

        String redirect = "";
        Movie searchedMovie = new Movie();
        if(movie.getCategory().equals("option1")) {
            List<Movie> searchedMovies = movieService.moviesByTitle(movie.getTitle());
            if(searchedMovies.size() == 1) {
                if (searchedMovies.get(0) == null) {
                    model.addAttribute("error", true);
                    return "redirect:/Cinema.html?error";
                }
                redirect = "/descriptions/" + searchedMovies.get(0).getMovieID();
                if (searchedMovies.get(0).getCategory().equals("Coming-Soon")) {
                    redirect = "/descriptions/" + searchedMovies.get(0).getMovieID();
                }
            }
            else{
                if(searchedMovies.size() == 0){
                    model.addAttribute("error", true);
                    return "redirect:/Cinema.html?error";
                }
                List<Movie> searchedGenreON = new ArrayList<>();
                List<Movie> searchedGenreCS = new ArrayList<>();

                for(Movie m : searchedMovies){
                    if (m.getCategory().equals("Coming-Soon")){
                        searchedGenreCS.add(m);
                    }
                    else {
                        searchedGenreON.add(m);
                    }
                }

                model.addAttribute("onmovies", searchedGenreON);
                model.addAttribute("csmovies", searchedGenreCS);
                model.addAttribute("searchedmovie", searchedMovie);
                return "CinemaGenre";
            }
        }
        else {
//            Movie searchedMovie = new Movie();
            List<Movie> searchedGenreON = movieService.getMoviesByGenreOutNow(movie.getTitle());
            List<Movie> searchedGenreCS = movieService.getMoviesByGenreComing(movie.getTitle());
            if(searchedGenreCS.size() ==0 && searchedGenreON.size() == 0){
                model.addAttribute("error", true);
                return "redirect:/Cinema.html?error";
            }
            model.addAttribute("onmovies", searchedGenreON);
            model.addAttribute("csmovies", searchedGenreCS);
            model.addAttribute("searchedmovie", searchedMovie);
            return "CinemaGenre";
        }
        httpResponse.sendRedirect(redirect);
        return null;
    }

}
