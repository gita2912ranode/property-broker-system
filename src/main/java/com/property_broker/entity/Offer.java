package com.property_broker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;



@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private Property property;

    @ManyToOne
    private User customer;

    private Double offerPrice;

    private String status; // PENDING, ACCEPTED, REJECTED, COUNTER

    private String message;

    private Instant createdAt = Instant.now();

    
}
