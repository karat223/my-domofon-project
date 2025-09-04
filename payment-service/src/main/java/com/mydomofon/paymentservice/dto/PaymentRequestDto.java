package com.mydomofon.paymentservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    private String username;
    private BigDecimal amount;
}
