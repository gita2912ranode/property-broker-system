package com.property_broker.controller;

import com.property_broker.entity.Role;
import com.property_broker.service.impl.RoleServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleServiceImpl service;

    public RoleController(RoleServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Role>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> get(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role role) {
        return ResponseEntity.ok(service.create(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable String id, @RequestBody Role role) {
        return ResponseEntity.ok(service.update(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}