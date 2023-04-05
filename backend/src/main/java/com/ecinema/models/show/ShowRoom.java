package com.ecinema.models.show;

import com.ecinema.models.seat.Seat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "showrooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowRoom {

    @Id
    @GeneratedValue
    @Column(name = "roomID")
    private int roomID;

    @Column(name = "roomname")
    private String roomName;

    @Column(name = "seatnum")
    private int seatNum;

    @OneToMany(mappedBy = "showRoom")
    private List<Show> showsTimes;

    @OneToMany(mappedBy = "showRoomSeating")
    private List<Seat> seats;
}
