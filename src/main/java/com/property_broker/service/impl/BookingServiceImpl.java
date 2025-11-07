package com.property_broker.service.impl;

import com.property_broker.dto.BookingDto;
import com.property_broker.entity.Booking;
import com.property_broker.entity.Property;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.BookingRepository;
import com.property_broker.repository.PropertyRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repo;
    private final PropertyRepository propertyRepo;
    private final UserRepository userRepo;

    private final ModelMapper modelMapper;

   

    public List<Booking> findAll() {
        return repo.findAll();
    }

    public Booking findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    @Transactional
    public Booking create(String propertyId, String customerId, BookingDto bookingDto) {
        Booking booking=modelMapper.map(bookingDto,Booking.class);
        Property property = propertyRepo.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        User customer = userRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        booking.setProperty(property);
        booking.setCustomer(customer);
        if (booking.getStatus() == null) booking.setStatus("REQUESTED");
        return repo.save(booking);
    }

    @Transactional
    public Booking updateStatus(String id, String status) {
        Booking existing = findById(id);
        existing.setStatus(status);
        return repo.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Booking not found: " + id);
        repo.deleteById(id);
    }

    public List<Booking> findByProperty(String propertyId) {
        return repo.findByPropertyId(propertyId);
    }

    public List<Booking> findByCustomer(String customerId) {
        return repo.findByCustomerId(customerId);
    }
}
