package com.property_broker.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PropertyDto {
    private String id;


    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "propertyType is required")
    private String propertyType;

    @NotNull(message = "price is required")
    private Double price;

   
    private String address;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "state is required")
    private String state;

    @NotBlank(message = "country is required")
    private String country;

    @NotBlank(message = "zipcode is required")
    private String zipcode;

    @NotNull(message = "bedrooms is required")
    private Integer bedrooms;

    @NotNull(message = "bathrooms is required")
    private Integer bathrooms;

    @NotNull(message = "areaSqft is required")
    private Integer areaSqft;

    private String imageUrl;



}
