package com.ecinema.models.seat;

import com.ecinema.models.show.ShowRoom;
import com.ecinema.models.ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "seats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue
    @Column(name = "seatID")
    private int seatID;

    @Column(name = "available")
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "roomID")
    private ShowRoom showRoomSeating;

    @OneToMany(mappedBy = "seat")
    private List<Ticket> tickets;
}
