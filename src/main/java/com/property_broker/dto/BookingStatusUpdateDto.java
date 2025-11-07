package com.property_broker.dto;

import com.property_broker.constants.BookingStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusUpdateDto {
    @NotNull(message = "Status is required")
    private BookingStatus status;
}
