package com.ecinema.services;

import com.ecinema.repositories.ShowRepository;
import com.ecinema.repositories.ShowRoomRepository;
import com.ecinema.show.Show;
import com.ecinema.show.ShowRoom;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.Format;
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

    @Autowired
    public ShowService(ShowRepository showRepository, MovieService movieService, ShowRoomRepository showRoomRepository) {
        this.showRepository = showRepository;
        this.movieService = movieService;
        this.showRoomRepository = showRoomRepository;
    }

    public List<Show> getSortedShows(int id) throws ParseException {
        List<Show> shows = showRepository.findAll(Sort.by("date").ascending().and(Sort.by("time").descending()));
        shows.removeIf(show -> show.getMovie().getMovieID() != id);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        if(!shows.get(0).getDate().contains(" ")){
            for(Show show: shows){
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(show.getDate());;
                String strDate = formatter.format(date);
                show.setDate(strDate);
                System.out.println("Date Format with dd MMMM yyyy : "+strDate);
            }
        }


        return shows;
    }

    public Show getShow(int id){
        return showRepository.findByShowID(id);
    }

    public Show getShowDisplayDate(int id) throws ParseException {
        Show show = showRepository.findByShowID(id);
        if(!show.getDate().contains(" ")){
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(show.getDate());;
            String strDate = formatter.format(date);
            show.setDate(strDate);
        }
        return show;
    }

    public String createShowTime(Show show){
        List<ShowRoom> showRooms = showRoomRepository.findAll();
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

        show.setShowRoom(showRooms.get(0));
        show.setMovie(movieService.getMovie(show.getMovie().getMovieID()));
        showRepository.save(show);
        return "redirect:/admin/manageMovies?scheduleSuccess";
    }
}
