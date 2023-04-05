package com.ecinema.controllers;

import com.ecinema.movie.Movie;
import com.ecinema.services.MovieService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class Webpage {

    private final MovieService movieService;

    @Autowired
    public Webpage(MovieService movieService) {
        this.movieService = movieService;
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
    public String getDescription(@PathVariable("id")int id, Model model){
        Movie movie = movieService.getMovie(id);
        Movie searchedMovie = new Movie();
        model.addAttribute("searchedmovie", searchedMovie);
        model.addAttribute("movie", movie);
        return "descriptionsNoAuth";
    }

    @PostMapping("/search")
    public String searchMovieNoAuth(HttpServletResponse httpResponse, @ModelAttribute("movie")Movie movie, Model model) throws IOException {
        String redirect = "";
        if(movie.getCategory().equals("option1")) {
            Movie searchedMovie = movieService.getMovieByTitle(movie.getTitle());
            if(searchedMovie == null){
                model.addAttribute("error", true);
                return "redirect:/Cinema.html?error";
            }
            redirect = "/descriptions/" + searchedMovie.getMovieID();
            if (searchedMovie.getCategory().equals("Coming-Soon")) {
                redirect = "/descriptions/" + searchedMovie.getMovieID();
            }
        }
        else {
            Movie searchedMovie = new Movie();
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
