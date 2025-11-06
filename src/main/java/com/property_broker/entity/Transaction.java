package com.property_broker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    private Offer offer;

    private Double amount;

    private String paymentMethod;

    private String status; // INITIATED, SUCCESS, FAILED

    private Instant createdAt = Instant.now();

   
}
