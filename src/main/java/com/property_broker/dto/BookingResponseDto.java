package com.property_broker.dto;

import java.time.LocalDateTime;

import com.property_broker.constants.BookingStatus;

import lombok.Data;

@Data
public class BookingResponseDto {
    private String id;
    private String propertyId;
    private String customerId;
    private LocalDateTime scheduledAt;
    private BookingStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
