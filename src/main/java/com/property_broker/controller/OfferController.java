package com.property_broker.controller;

import com.property_broker.entity.Offer;
import com.property_broker.service.impl.OfferServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferServiceImpl service;

    public OfferController(OfferServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Offer>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> get(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Create offer for property by a customer:
     * POST /api/offers?propertyId={prop}&customerId={cust}
     */
    @PostMapping
    public ResponseEntity<Offer> create(@RequestParam String propertyId,
                                        @RequestParam String customerId,
                                        @RequestBody Offer offer) {
        return ResponseEntity.ok(service.create(propertyId, customerId, offer));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Offer> updateStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Offer>> byProperty(@PathVariable String propertyId) {
        return ResponseEntity.ok(service.findByProperty(propertyId));
    }
}
