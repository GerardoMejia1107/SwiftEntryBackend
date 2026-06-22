package com.gerardo.swiftentrybackend.domain.Ticket.repositories;

import com.gerardo.swiftentrybackend.domain.Ticket.TicketTransferModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketTransferRepository extends JpaRepository<TicketTransferModel, Integer> {

    List<TicketTransferModel> findByTicket_Id(Integer ticketId);
}
