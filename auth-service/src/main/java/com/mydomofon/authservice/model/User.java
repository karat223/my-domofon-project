package com.mydomofon.authservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users") // Явно указываем имя таблицы в БД
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING) // Храним роль как строку (например, "ROLE_USER")
    private ERole role;
}
