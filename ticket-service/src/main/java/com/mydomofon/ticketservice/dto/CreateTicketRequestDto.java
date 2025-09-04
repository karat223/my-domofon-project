package com.mydomofon.ticketservice.dto;

import lombok.Data;

@Data
public class CreateTicketRequestDto {
    private String username;
    private String title;
    private String description;
}