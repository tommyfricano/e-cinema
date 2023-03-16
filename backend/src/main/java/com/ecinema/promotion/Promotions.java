package com.ecinema.promotion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Promotions(String code, String startDate, String endDate) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
