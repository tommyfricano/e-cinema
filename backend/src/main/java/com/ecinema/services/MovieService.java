package com.ecinema.services;

import com.ecinema.movie.Movie;
import com.ecinema.repositories.MovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie getMovie(int id){
        return movieRepository.getMovieByMovieID(id);
    }
    public List<Movie> getMovies(){
        return movieRepository.findAll();
    }

    public List<Movie> getMoviesOutNow(){
        return movieRepository.findByCategory("Now-Showing");
    }

    public List<Movie> getMoviesComingSoon(){
        return movieRepository.findByCategory("Coming-Soon");
    }


    public String saveMovie(Movie movie){
        Movie checkMovie = movieRepository.getMovieByTitle(movie.getTitle());
        if(checkMovie != null){
            return "error";
        }
        movieRepository.save(movie);
        return "/admin/manageMovies?success";
    }

    public String editMovie(int id, Movie movie){
        Movie movieToUpdate = movieRepository.getMovieByMovieID(id);
        movieToUpdate.setTitle(movie.getTitle());
        movieToUpdate.setMovieImage(movie.getMovieImage());
        movieToUpdate.setCast(movie.getCast());
        movieToUpdate.setCategory(movie.getCategory());
        movieToUpdate.setDirector(movie.getDirector());
        movieToUpdate.setProducer(movie.getProducer());
        movieToUpdate.setRating(movie.getRating());
        movieToUpdate.setReview(movie.getReview());
        movieToUpdate.setSynopsis(movie.getSynopsis());
        movieRepository.save(movieToUpdate);
        return "/admin/manageMovies?success";
    }

    public void deleteMovie(int id){
        movieRepository.deleteByMovieID(id);
    }
}
