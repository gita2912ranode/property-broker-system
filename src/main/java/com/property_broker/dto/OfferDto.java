package com.property_broker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OfferDto {
    private String id;

    @NotBlank(message = "property id is required")
    private String propertyId;

    @NotBlank(message = "customer id is required")
    private String customerId;

    @NotNull(message = "offerPrice is required")
    private Double offerPrice;

    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "message is required")
    private String message;



}
