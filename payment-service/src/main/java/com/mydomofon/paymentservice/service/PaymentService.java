package com.mydomofon.paymentservice.service;

import com.mydomofon.paymentservice.dto.PaymentRequestDto;
import com.mydomofon.paymentservice.model.Payment;
import com.mydomofon.paymentservice.model.PaymentStatus;
import com.mydomofon.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Payment createPayment(PaymentRequestDto paymentRequestDto) {
        // 1. Создаем объект платежа
        Payment payment = new Payment();
        payment.setUsername(paymentRequestDto.getUsername());
        payment.setAmount(paymentRequestDto.getAmount());
        payment.setStatus(PaymentStatus.NEW); // Статус "Новый"
        payment.setCreatedAt(LocalDateTime.now());

        // 2. Сохраняем платеж в базу данных
        Payment savedPayment = paymentRepository.save(payment);

        // 3. Отправляем сообщение в Kafka
        // Мы отправим ID платежа в топик 'payment_topic'
        kafkaTemplate.send("payment_topic", "New payment created: " + savedPayment.getId());

        return savedPayment;
    }
}
