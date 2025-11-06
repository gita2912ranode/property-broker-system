package com.property_broker.controller;

import com.property_broker.entity.Property;
import com.property_broker.service.impl.PropertyServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyServiceImpl service;

    public PropertyController(PropertyServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Property>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> get(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Create a property. Optionally pass ?ownerId=<uuid> to link an owner.
     */
    @PostMapping
    public ResponseEntity<Property> create(@RequestBody Property property,
                                           @RequestParam(required = false) String ownerId) {
        return ResponseEntity.ok(service.create(property, ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> update(@PathVariable String id, @RequestBody Property p) {
        return ResponseEntity.ok(service.update(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

