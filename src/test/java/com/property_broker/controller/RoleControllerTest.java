package com.property_broker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property_broker.dto.RoleDto;
import com.property_broker.entity.Role;
import com.property_broker.service.impl.RoleServiceImpl;

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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RoleServiceImpl roleService;

    @InjectMocks
    private RoleController roleController;

    private Role sampleRole;
    private RoleDto sampleRoleDto;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();

        // ----- ENTITY -----
        sampleRole = new Role();
        sampleRole.setId("1");
        sampleRole.setName("ROLE_ADMIN");

        // ----- VALID DTO -----
        sampleRoleDto = new RoleDto();
        sampleRoleDto.setId("1");
        sampleRoleDto.setName("ROLE_ADMIN");
    }

    // ----------------------- GET ALL ROLES -----------------------
    @Test
    void getAllRoles_ReturnsList() throws Exception {
        when(roleService.findAll()).thenReturn(Arrays.asList(sampleRole));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("ROLE_ADMIN")));

        verify(roleService, times(1)).findAll();
    }

    // ----------------------- GET ROLE BY ID -----------------------
    @Test
    void getRoleById_ReturnsRole() throws Exception {
        when(roleService.findById("1")).thenReturn(sampleRole);

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")));

        verify(roleService, times(1)).findById("1");
    }

    // ----------------------- CREATE ROLE -----------------------
    @Test
    void createRole_ReturnsCreated() throws Exception {
        when(roleService.create(any(RoleDto.class))).thenReturn(sampleRole);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRoleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")));

        verify(roleService, times(1)).create(any(RoleDto.class));
    }

    // ----------------------- UPDATE ROLE -----------------------
    @Test
    void updateRole_ReturnsUpdated() throws Exception {
        when(roleService.update(eq("1"), any(RoleDto.class))).thenReturn(sampleRole);

        mockMvc.perform(put("/api/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRoleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")));

        verify(roleService, times(1)).update(eq("1"), any(RoleDto.class));
    }

    // ----------------------- DELETE ROLE -----------------------
    @Test
    void deleteRole_ReturnsNoContent() throws Exception {
        doNothing().when(roleService).delete("1");

        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).delete("1");
    }
}
 
