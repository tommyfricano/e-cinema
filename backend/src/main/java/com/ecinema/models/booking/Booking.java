package com.ecinema.models.booking;

import com.ecinema.models.payment.PaymentCards;
import com.ecinema.models.promotion.Promotions;
import com.ecinema.models.show.Show;
import com.ecinema.models.ticket.Ticket;
import com.ecinema.models.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Timer;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NonNull
public class Booking {

    @Id
    @GeneratedValue
    @Column(name = "bookingID")
    private int bookingID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "showID")
    private Show show;

    @ManyToOne
    @JoinColumn(name = "paymentID")
    private PaymentCards paymentCards;

    @ManyToOne
    @JoinColumn(name = "promoID")
    private Promotions promotions;

    @Column(name = "total")
    private double total;

    private double finalTotal;;

    @OneToMany(mappedBy = "booking")
    private List<Ticket> tickets;


    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
