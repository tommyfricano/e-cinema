package com.ecinema.controllers;

import com.ecinema.movie.Movie;
import com.ecinema.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        topMovies.remove(0);
        Movie mainMovie = topMovies.get(0);
        model.addAttribute("mainmovie",mainMovie);
        model.addAttribute("topmovies",topMovies);
        model.addAttribute("onmovies", onMovies);
        model.addAttribute("csmovies", csMovies);
        return "Cinema";
    }

    @GetMapping("/descriptions/{id}")
    public String getDescription(@PathVariable("id")int id, Model model){
        Movie movie = movieService.getMovie(id);
        model.addAttribute("movie", movie);
        return "descriptionsMain";
    }

}
