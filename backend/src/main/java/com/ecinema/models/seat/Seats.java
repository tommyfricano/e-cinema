package com.ecinema.models.seat;

import com.ecinema.models.show.ShowRoom;
import com.ecinema.models.ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Seats {

    private int seatID;

    private boolean available;

    private ShowRoom showRoomSeating;

    private int seatNO;
}
