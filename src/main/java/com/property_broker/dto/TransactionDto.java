package com.property_broker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionDto {
    private String id;

    @NotBlank(message = "offer id is required")
    private String offerId;

    @NotNull(message = "amount is required")
    private Double amount;

    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    @NotBlank(message = "status is required")
    private String status;



}
