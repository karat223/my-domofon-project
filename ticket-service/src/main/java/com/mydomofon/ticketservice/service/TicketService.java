package com.mydomofon.ticketservice.service;

import com.mydomofon.ticketservice.dto.CreateTicketRequestDto;
import com.mydomofon.ticketservice.model.Ticket;
import com.mydomofon.ticketservice.model.TicketStatus;
import com.mydomofon.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public TicketService(TicketRepository ticketRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.ticketRepository = ticketRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Метод для создания новой заявки
    public Ticket createTicket(CreateTicketRequestDto requestDto) {
        Ticket ticket = new Ticket();
        ticket.setUsername(requestDto.getUsername());
        ticket.setTitle(requestDto.getTitle());
        ticket.setDescription(requestDto.getDescription());
        ticket.setStatus(TicketStatus.OPEN); // Новая заявка всегда открыта
        ticket.setCreatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    // Метод для обновления статуса заявки
    public Optional<Ticket> updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            ticket.setStatus(newStatus);
            Ticket updatedTicket = ticketRepository.save(ticket);

            // Отправляем событие в Kafka
            String message = String.format("Ticket %d status changed to %s", updatedTicket.getId(), updatedTicket.getStatus());
            kafkaTemplate.send("ticket_status_topic", message);

            return Optional.of(updatedTicket);
        }
        return Optional.empty();
    }

    // Метод для получения всех заявок
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}