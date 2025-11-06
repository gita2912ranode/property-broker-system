package com.property_broker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private Property property;

    @ManyToOne
    private User customer;

    private Instant scheduledAt;

    private String status; // PENDING, CONFIRMED, REJECTED

    private String notes;

    private Instant createdAt = Instant.now();

    
}
