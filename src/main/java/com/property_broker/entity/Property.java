package com.property_broker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private String propertyType;

    private Double price;

    private String address;

    private String city;

    private String state;

    private String country;

    private String zipcode;

    private Integer bedrooms;

    private Integer bathrooms;

    private Integer areaSqft;

    private String status = "AVAILABLE";

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PropertyImage> images;

    
}
