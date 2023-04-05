package com.ecinema.models.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name ="tickettypes")
public class TicketType {

    @Id
    @GeneratedValue
    @Column(name = "typeID")
    private int typeID;

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "type")
    private List<Ticket> tickets;
}
