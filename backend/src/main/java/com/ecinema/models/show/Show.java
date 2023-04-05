package com.ecinema.models.show;

import com.ecinema.models.booking.Booking;
import com.ecinema.models.movie.Movie;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue
    @Column(name = "showID")
    private int showID;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private int time;

    @ManyToOne
    @JoinColumn(name = "movieID")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "roomID")
    private ShowRoom showRoom;

    @OneToMany(mappedBy = "show")
    private List<Booking> bookings;
}
