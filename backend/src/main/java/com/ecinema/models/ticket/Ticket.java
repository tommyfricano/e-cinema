package com.ecinema.models.ticket;

import com.ecinema.models.booking.Booking;
import com.ecinema.models.seat.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@NonNull
@Table(name ="tickets")
public class Ticket {
    @Id
    @GeneratedValue
    @Column(name = "ticketID")
    private int ticketID;

    @ManyToOne
    @JoinColumn(name = "bookingID")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seatID")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "typeID")
    private TicketType type;

    @Column(name = "price")
    private double price;

    @Column(name= "checked", nullable = false)
    private int check;
}
