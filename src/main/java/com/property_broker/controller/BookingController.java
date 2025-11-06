package com.property_broker.controller;

import com.property_broker.entity.Booking;
import com.property_broker.service.impl.BookingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingServiceImpl service;

    public BookingController(BookingServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> get(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Create booking: POST /api/bookings?propertyId=...&customerId=...
     */
    @PostMapping
    public ResponseEntity<Booking> create(@RequestParam String propertyId, @RequestParam String customerId, @RequestBody Booking booking) {
        return ResponseEntity.ok(service.create(propertyId, customerId, booking));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
