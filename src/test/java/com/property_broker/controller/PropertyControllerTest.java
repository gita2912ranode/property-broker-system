package com.property_broker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property_broker.dto.PropertyDto;
import com.property_broker.entity.Property;
import com.property_broker.service.impl.PropertyServiceImpl;

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
class PropertyControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PropertyServiceImpl propertyService;

    @InjectMocks
    private PropertyController propertyController;

    private Property sampleProperty;
    private PropertyDto sampleDto;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(propertyController).build();

        sampleProperty = new Property();
        sampleProperty.setId("1");
        sampleProperty.setTitle("Luxury Villa");

        // ----- VALID PropertyDto (NO VALIDATION FAILURES) -----
        sampleDto = new PropertyDto();
        sampleDto.setId("1");
        sampleDto.setTitle("Luxury Villa");
        sampleDto.setDescription("Beautiful villa with garden");
        sampleDto.setPropertyType("House");
        sampleDto.setPrice(500000.0);
        sampleDto.setAddress("123 Street");
        sampleDto.setCity("Mumbai");
        sampleDto.setState("Maharashtra");
        sampleDto.setCountry("India");
        sampleDto.setZipcode("400001");
        sampleDto.setBedrooms(3);
        sampleDto.setBathrooms(2);
        sampleDto.setAreaSqft(2000);
        sampleDto.setImageUrl("http://example.com/image.jpg");
    }

    // -------------------- GET ALL --------------------
    @Test
    void getAllProperties_ReturnsList() throws Exception {
        when(propertyService.findAll()).thenReturn(Arrays.asList(sampleProperty));

        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].title", is("Luxury Villa")));

        verify(propertyService, times(1)).findAll();
    }

    // -------------------- GET BY ID --------------------
    @Test
    void getPropertyById_ReturnsProperty() throws Exception {
        when(propertyService.findById("1")).thenReturn(sampleProperty);

        mockMvc.perform(get("/api/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")));

        verify(propertyService, times(1)).findById("1");
    }

    // -------------------- CREATE PROPERTY --------------------


    // -------------------- UPDATE PROPERTY --------------------
    @Test
    void updateProperty_ReturnsUpdated() throws Exception {
        when(propertyService.update(eq("1"), any(PropertyDto.class))).thenReturn(sampleProperty);

        mockMvc.perform(put("/api/properties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")));

        verify(propertyService, times(1)).update(eq("1"), any(PropertyDto.class));
    }

    // -------------------- DELETE PROPERTY --------------------
    @Test
    void deleteProperty_ReturnsNoContent() throws Exception {
        doNothing().when(propertyService).delete("1");

        mockMvc.perform(delete("/api/properties/1"))
                .andExpect(status().isNoContent());

        verify(propertyService, times(1)).delete("1");
    }

   
    @Test
    void getPropertiesByOwner_ReturnsList() throws Exception {
        when(propertyService.findByOwnerId()).thenReturn(Arrays.asList(sampleProperty));

        mockMvc.perform(get("/api/properties/owner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")));

        verify(propertyService, times(1)).findByOwnerId();
    }
}
 