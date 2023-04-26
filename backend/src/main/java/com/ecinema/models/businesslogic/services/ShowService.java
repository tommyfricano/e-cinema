package com.ecinema.models.businesslogic.services;

import com.ecinema.models.seat.Seat;
import com.ecinema.models.businesslogic.repositories.SeatRepository;
import com.ecinema.models.businesslogic.repositories.ShowRepository;
import com.ecinema.models.businesslogic.repositories.ShowRoomRepository;
import com.ecinema.models.show.Show;
import com.ecinema.models.show.ShowRoom;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieService movieService;

    private final ShowRoomRepository showRoomRepository;

    private final SeatRepository seatRepository;

    @Autowired
    public ShowService(ShowRepository showRepository, MovieService movieService, ShowRoomRepository showRoomRepository, SeatRepository seatRepository) {
        this.showRepository = showRepository;
        this.movieService = movieService;
        this.showRoomRepository = showRoomRepository;
        this.seatRepository = seatRepository;
    }

    public List<Seat> getSeatCopy(Show show, String dateTime){
        return seatRepository.findAllByShowRoomSeatingAndDateTime(show.getShowRoom(), dateTime);
    }
    public List<Show> getSortedShows(int id) throws ParseException {
        List<Show> shows = showRepository.findAll(Sort.by("date").ascending().and(Sort.by("time").descending()));
        shows.removeIf(show -> show.getMovie().getMovieID() != id);

        return shows;
    }

    public Show getShow(int id){
        return showRepository.findByShowID(id);
    }


    public String createShowTime(Show show) throws ParseException {
        List<ShowRoom> showRooms = showRoomRepository.findAll();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(show.getDate());;
        String strDate = formatter.format(date);
        show.setDate(strDate);

        List<Show> shows = showRepository.findAllByDateAndTime(show.getDate(),show.getTime());

        int j=0;

        for(int i=0;i < shows.size();i++){

            if(shows.get(i).getShowRoom().getRoomID() == showRooms.get(j).getRoomID()){
                showRooms.remove(j);
                if(j > 0)
                    j--;
            }
            else {
                j++;
            }
        }

        if(showRooms.size() == 0){
            return "redirect:/admin/scheduleMovie?error";
        }
        List<Seat> seats = new ArrayList<>();
        for(int i =0; i < 48;i++){
            Seat seat = new Seat();
            seat.setAvailable(false);
            seat.setShowRoomSeating(showRooms.get(0));
            seat.setDateTime(strDate+":"+show.getTime());
            seat.setSeatNO(i+1);
            seats.add(seat);
        }
        showRooms.get(0).setSeats(seats);
        seatRepository.saveAll(seats);
        showRoomRepository.save(showRooms.get(0));
        show.setShowRoom(showRooms.get(0));
        show.setMovie(movieService.getMovie(show.getMovie().getMovieID()));

        showRepository.save(show);
        return "redirect:/admin/manageMovies?scheduleSuccess";
    }
}
