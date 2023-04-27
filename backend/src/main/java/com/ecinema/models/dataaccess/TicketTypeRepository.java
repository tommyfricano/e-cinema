package com.ecinema.models.dataaccess;

import com.ecinema.models.ticket.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {

    public TicketType getTicketTypeByType(String type);
}
