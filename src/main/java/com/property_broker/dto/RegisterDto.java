package com.property_broker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class RegisterDto {
 
    @NotBlank(message = "Username is required")
    private String username;
 
    @NotBlank(message = "Password is required")
    private String password;
 
    @NotBlank(message = "First name is required")
    private String firstName;
 
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Last name is required")
    private String phoneNo;
 
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;
 
    private String role; // Optional (e.g. "OWNER", "BROKER", defaults to CUSTOMER)
}
