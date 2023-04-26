package com.ecinema.models.businesslogic.repositories;

import com.ecinema.models.show.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Integer> {

    public List<Show> findAllByDateAndTime(String date, int time);

    public Show findByShowID(int id);

//    public List<Show> findAllByMovieAndOrderByDate

}
