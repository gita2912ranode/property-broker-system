package com.property_broker.service;

import com.property_broker.dto.BookingDto;
import com.property_broker.entity.Booking;

import java.util.List;

public interface BookingService {
    List<Booking> findAll();
    Booking findById(String id);
    Booking create(String propertyId, BookingDto booking);
    Booking updateStatus(String id, String status);
    void delete(String id);
    List<Booking> findByCustomer();
}
