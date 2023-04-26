package com.ecinema.models.businesslogic.services;

import com.ecinema.models.movie.Movie;
import com.ecinema.models.businesslogic.repositories.MovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Movie getMovieByTitle(String title){
        return movieRepository.getMovieByTitleIgnoreCase(title);
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

    public List<Movie> getMoviesByGenreOutNow(String genre){
        return movieRepository.findByGenreIgnoreCaseAndAndCategory(genre, "Now-Showing");
    }
    public List<Movie> getMoviesByGenreComing(String genre){
        return movieRepository.findByGenreIgnoreCaseAndAndCategory(genre, "Coming-Soon");
    }

    public List<Movie> moviesByTitle(String title){
        List<Movie> movies = movieRepository.findAll();
        List<Movie> finalMovies = new ArrayList<>();

        for(Movie movie : movies){
            if(movie.getTitle().toLowerCase().contains(title.toLowerCase())){
                finalMovies.add(movie);
            }
        }

        return finalMovies;
    }

    public List<Movie> getTopMovies(){
        List<Movie> movies = movieRepository.findByCategory("Now-Showing");
        if(movies.size() > 4 ){
            List<Movie> newList = new ArrayList<>();
            for(int i=0;i<4;i++) {
                newList.add(movies.get(i));
            }
            return newList;
        }
        return movies;
    }


    public String saveMovie(Movie movie){
        Movie checkMovie = movieRepository.getMovieByTitleIgnoreCase(movie.getTitle());
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
        movieToUpdate.setTrailerVideo(movie.getTrailerVideo());
        movieToUpdate.setGenre(movie.getGenre());
        movieRepository.save(movieToUpdate);
        return "/admin/manageMovies?success";
    }

    public void deleteMovie(int id){
        movieRepository.deleteByMovieID(id);
    }
}
