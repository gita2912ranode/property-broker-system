package com.property_broker.controller;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.property_broker.dto.LoginRequest;
import com.property_broker.dto.RegisterDto;
import com.property_broker.dto.ResponseDto;
import com.property_broker.entity.Role;
import com.property_broker.entity.User;
import com.property_broker.repository.RoleRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.security.JwtUtil;
import com.property_broker.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
 
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; 
    private final UserService userService;
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
 

    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
 
        boolean ok = userService.checkUserCredentails(username, password);
        if (!ok) {
            log.warn("Unauthorized login attempt: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
 
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails);
 
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("roles", userDetails.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList()));
 
        log.info("User logged in: {}", username);
        return ResponseEntity.ok(response);
    }
 
 
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterDto request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new ResponseDto("409","Username already exists"));
        }
 
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ResponseDto("409","Email already exists"));
        }
 
        // Default role is CUSTOMER
        String roleName = (request.getRole() != null) ? request.getRole().toUpperCase() : "CUSTOMER";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
 
        Set<Role> roles = new HashSet<>();
        roles.add(role);
 
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhoneNo());
        newUser.setRoles(roles);
 
        userRepository.save(newUser);
 
        
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto("201","User Register Successfully") );
    }
}