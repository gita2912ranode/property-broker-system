package com.property_broker.controller;

import com.property_broker.dto.UserDto;
import com.property_broker.entity.User;
import com.property_broker.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserServiceImpl service;

    public UserController(UserServiceImpl service) {
        this.service = service;
    } 

    @GetMapping
    public ResponseEntity<List<User>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(service.create(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody UserDto user) {
        return ResponseEntity.ok(service.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<User> addRole(@PathVariable String id, @RequestParam String role) {
        return ResponseEntity.ok(service.assignRole(id, role));
    }

    @DeleteMapping("/{id}/roles")
    public ResponseEntity<User> removeRole(@PathVariable String id, @RequestParam String role) {
        return ResponseEntity.ok(service.removeRole(id, role));
    }
}
