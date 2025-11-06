package com.property_broker.controller;

import com.property_broker.entity.Transaction;
import com.property_broker.service.impl.TransactionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionServiceImpl service;

    public TransactionController(TransactionServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> get(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Create transaction: POST /api/transactions?offerId=...
     */
    @PostMapping
    public ResponseEntity<Transaction> create(@RequestParam String offerId, @RequestBody Transaction tx) {
        return ResponseEntity.ok(service.create(offerId, tx));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}