package com.property_broker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property_broker.dto.UserDto;
import com.property_broker.entity.User;
import com.property_broker.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    private User sampleUser;
    private UserDto sampleUserDto;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        sampleUser = new User();
        sampleUser.setId("1");
        sampleUser.setUsername("john");
        sampleUser.setEmail("john@example.com");

        sampleUserDto = new UserDto();
        sampleUserDto.setUsername("john");
        sampleUserDto.setEmail("john@example.com");
    }

    // ----------------------- GET ALL USERS -----------------------
    @Test
    void getAllUsers_ReturnsList() throws Exception {
        List<User> users = Arrays.asList(sampleUser);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].username", is("john")));

        verify(userService, times(1)).findAll();
    }

    // ----------------------- GET USER BY ID -----------------------
    @Test
    void getUserById_ReturnsUser() throws Exception {
        when(userService.findById("1")).thenReturn(sampleUser);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.username", is("john")));

        verify(userService, times(1)).findById("1");
    }

    

    // ----------------------- UPDATE USER -----------------------
    @Test
    void updateUser_ReturnsUpdatedUser() throws Exception {
        when(userService.update(eq("1"), any(UserDto.class))).thenReturn(sampleUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.username", is("john")));

        verify(userService, times(1)).update(eq("1"), any(UserDto.class));
    }

    // ----------------------- DELETE USER -----------------------
    @Test
    void deleteUser_ReturnsNoContent() throws Exception {
        doNothing().when(userService).delete("1");

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete("1");
    }

    // ----------------------- ADD ROLE -----------------------
    @Test
    void addRole_ReturnsUpdatedUser() throws Exception {
        when(userService.assignRole("1", "ADMIN")).thenReturn(sampleUser);

        mockMvc.perform(post("/api/users/1/roles")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")));

        verify(userService, times(1)).assignRole("1", "ADMIN");
    }

    // ----------------------- REMOVE ROLE -----------------------
    @Test
    void removeRole_ReturnsUpdatedUser() throws Exception {
        when(userService.removeRole("1", "ADMIN")).thenReturn(sampleUser);

        mockMvc.perform(delete("/api/users/1/roles")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")));

        verify(userService, times(1)).removeRole("1", "ADMIN");
    }
}
 