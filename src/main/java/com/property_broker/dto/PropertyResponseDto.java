package com.property_broker.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class PropertyResponseDto {
    private String id;
    private String title;
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
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private String imageUrl;

    // Owner details (only required fields)
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;
    private String ownerPhone;
}