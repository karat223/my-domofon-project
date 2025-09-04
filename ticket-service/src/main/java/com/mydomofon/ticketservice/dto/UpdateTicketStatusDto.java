package com.mydomofon.ticketservice.dto;

import com.mydomofon.ticketservice.model.TicketStatus;
import lombok.Data;

@Data
public class UpdateTicketStatusDto {
    private TicketStatus status;
}