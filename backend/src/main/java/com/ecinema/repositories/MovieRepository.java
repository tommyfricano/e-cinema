package com.ecinema.repositories;

import com.ecinema.models.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    public Movie getMovieByTitleIgnoreCase(String title);

    public Movie getMovieByMovieID(int id);

    public void deleteByMovieID(int id);

    public List<Movie> findByCategory(String category);

    public List<Movie> findByGenreIgnoreCaseAndAndCategory(String genre, String category);


}
