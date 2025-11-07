package com.property_broker.controller;

import com.property_broker.dto.PropertyDto;
import com.property_broker.entity.Property;
import com.property_broker.service.impl.PropertyServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Property> create(@Valid @RequestBody PropertyDto propertyDto,
                                           @RequestParam String ownerId) {
        return ResponseEntity.ok(service.create(propertyDto, ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> update(@PathVariable String id, @Valid @RequestBody PropertyDto propertyDto) {
        return ResponseEntity.ok(service.update(id,propertyDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Property>> searchProperties(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false, defaultValue = "price") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Sort sort = Sort.by(
                Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC),
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Property> result = service.searchPropertiesPaginated(
                type, minPrice, maxPrice, city, state,
                bedrooms, bathrooms, pageable
        );

        return ResponseEntity.ok(result);
    }
}

