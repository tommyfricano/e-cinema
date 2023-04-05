package com.ecinema.models.promotion;

import com.ecinema.models.booking.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "promotions")
public class Promotions {

    @Id
    @GeneratedValue
    @Column(name = "promoID")
    private int promoID;

    @Column(name = "code")
    private String code;

    @Column(name = "startdate")
    private String startDate;

    @Column(name = "enddate")
    private String endDate;

    @Column(name = "discount")
    private double discount;

    @Column(name = "sent")
    private boolean sent;

    @OneToMany(mappedBy = "promotions")
    private List<Booking> bookings;

    public Promotions(String code, String startDate, String endDate) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
