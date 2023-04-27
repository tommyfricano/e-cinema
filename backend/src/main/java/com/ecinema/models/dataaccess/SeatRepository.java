package com.ecinema.models.dataaccess;

import com.ecinema.models.seat.Seat;
import com.ecinema.models.seat.Seats;
import com.ecinema.models.show.ShowRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    public Seat getSeatBySeatID(int seatID);

    public List<Seat> findAllByShowRoomSeating(ShowRoom showRoom);
    public List<Seat> findAllByShowRoomSeatingAndDateTime(ShowRoom showRoom, String dateTime);

}
