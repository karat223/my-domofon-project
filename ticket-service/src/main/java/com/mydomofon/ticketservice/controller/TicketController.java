package com.mydomofon.ticketservice.controller;

import com.mydomofon.ticketservice.dto.CreateTicketRequestDto;
import com.mydomofon.ticketservice.dto.UpdateTicketStatusDto;
import com.mydomofon.ticketservice.model.Ticket;
import com.mydomofon.ticketservice.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/create")
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketRequestDto requestDto) {
        Ticket newTicket = ticketService.createTicket(requestDto);
        return ResponseEntity.ok(newTicket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateTicketStatus(@PathVariable Long id, @RequestBody UpdateTicketStatusDto statusDto) {
        Optional<Ticket> updatedTicket = ticketService.updateTicketStatus(id, statusDto.getStatus());
        return updatedTicket.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}