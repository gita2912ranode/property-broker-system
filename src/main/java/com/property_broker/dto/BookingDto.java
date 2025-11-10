package com.property_broker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private String id;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledAt;
 

    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "notes is required")
    private String notes;



}
