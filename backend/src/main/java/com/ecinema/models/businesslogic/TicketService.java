package com.ecinema.models.businesslogic;

import com.ecinema.models.ticket.Ticket;
import com.ecinema.models.ticket.TicketType;
import com.ecinema.models.dataaccess.TicketRepository;
import com.ecinema.models.dataaccess.TicketTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TicketService {
    private final TicketRepository ticketRepository;

    private final TicketTypeRepository ticketTypeRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, TicketTypeRepository ticketTypeRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    public List<Ticket> getTickets(){
        return ticketRepository.findAll();
    }

    public TicketType getTicketType(String type){
        return ticketTypeRepository.getTicketTypeByType(type);
    }

    public void saveTicket(Ticket ticket){
        ticketRepository.save(ticket);
    }
}
