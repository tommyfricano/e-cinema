package com.ecinema.repositories;

import com.ecinema.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    public Movie getMovieByTitle(String title);

    public Movie getMovieByMovieID(int id);

    public void deleteByMovieID(int id);

    public List<Movie> findByCategory(String category);
}
