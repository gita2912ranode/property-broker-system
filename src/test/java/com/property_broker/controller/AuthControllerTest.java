package com.property_broker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property_broker.dto.LoginRequest;
import com.property_broker.dto.RegisterDto;
import com.property_broker.entity.Role;
import com.property_broker.entity.User;
import com.property_broker.repository.RoleRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.security.JwtUtil;
import com.property_broker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // ----------------------- LOGIN TESTS -----------------------

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        LoginRequest request = new LoginRequest("john", "pass");

        when(userService.checkUserCredentails("john", "pass")).thenReturn(true);

        UserDetails mockUser = org.springframework.security.core.userdetails.User
                .builder()
                .username("john")
                .password("encoded")
                .roles("CUSTOMER")
                .build();

        when(userDetailsService.loadUserByUsername("john")).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser)).thenReturn("mock-jwt");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt")))
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.roles[0]", is("ROLE_CUSTOMER")));

        verify(userService, times(1)).checkUserCredentails("john", "pass");
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("john", "wrong");

        when(userService.checkUserCredentails("john", "wrong")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Invalid credentials")));

        verify(userService, times(1)).checkUserCredentails("john", "wrong");
    }

    // ----------------------- REGISTER TESTS -----------------------

    @Test
    void register_ValidRequest_ReturnsCreated() throws Exception {

        RegisterDto dto = new RegisterDto();
        dto.setUsername("ram");
        dto.setEmail("ram@gmail.com");
        dto.setPassword("12345");
        dto.setPhoneNo("9999999999");
        dto.setFirstName("Ram");
        dto.setLastName("Kumar");
        dto.setRole("CUSTOMER");

        when(userRepository.findByUsername("ram")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ram@gmail.com")).thenReturn(Optional.empty());

        Role role = new Role("1", "CUSTOMER");
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("12345")).thenReturn("encoded123");

        User savedUser = new User();
        savedUser.setId("123");
        savedUser.setUsername("ram");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode", is("201")))
                .andExpect(jsonPath("$.statusMsg", is("User Register Successfully")));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UsernameExists_ReturnsBadRequest() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("existing");
        dto.setEmail("aa@gmail.com");
        dto.setPassword("123");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setPhoneNo("999");

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusMsg", is("Username already exists")));

        verify(userRepository, times(1)).findByUsername("existing");
    }
}
 