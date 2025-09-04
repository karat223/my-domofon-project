package com.mydomofon.paymentservice.controller;


import com.mydomofon.paymentservice.dto.PaymentRequestDto;
import com.mydomofon.paymentservice.model.Payment;
import com.mydomofon.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments") // Устанавливаем базовый путь
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        Payment newPayment = paymentService.createPayment(paymentRequestDto);
        return ResponseEntity.ok(newPayment);
    }
}
