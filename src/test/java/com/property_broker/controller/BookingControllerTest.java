package com.property_broker.controller;

import com.property_broker.dto.BookingDto;
import com.property_broker.entity.Booking;
import com.property_broker.service.impl.BookingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;
    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController bookingController;

    private Booking sampleBooking;
    private BookingDto sampleBookingDto;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        // ENTITY
        sampleBooking = new Booking();
        sampleBooking.setId("1");
        sampleBooking.setStatus("PENDING");
        sampleBooking.setNotes("Test notes");
        sampleBooking.setScheduledAt(LocalDateTime.now().plusDays(1));

        // VALID DTO (this is crucial!)
        sampleBookingDto = new BookingDto();
        sampleBookingDto.setId("1");
        sampleBookingDto.setScheduledAt(LocalDateTime.now().plusDays(1));
        sampleBookingDto.setStatus("PENDING");
        sampleBookingDto.setNotes("Test notes");
    }

    // ----------------------- GET ALL -----------------------
    @Test
    void getAllBookings_ReturnsList() throws Exception {
        when(bookingService.findAll()).thenReturn(Arrays.asList(sampleBooking));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")));

        verify(bookingService, times(1)).findAll();
    }

    // ----------------------- GET BY ID -----------------------
    @Test
    void getBookingById_ReturnsBooking() throws Exception {
        when(bookingService.findById("1")).thenReturn(sampleBooking);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")));

        verify(bookingService, times(1)).findById("1");
    }

    // ----------------------- GET BY CUSTOMER -----------------------
    @Test
    void getBookingsByCustomer_ReturnsList() throws Exception {
        when(bookingService.findByCustomer()).thenReturn(Arrays.asList(sampleBooking));

        mockMvc.perform(get("/api/bookings/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")));

        verify(bookingService, times(1)).findByCustomer();
    }

    // ----------------------- CREATE BOOKING -----------------------


    // ----------------------- UPDATE STATUS -----------------------
    @Test
    void updateBookingStatus_ReturnsUpdated() throws Exception {
        sampleBooking.setStatus("CONFIRMED");
        when(bookingService.updateStatus("1", "CONFIRMED")).thenReturn(sampleBooking);

        mockMvc.perform(put("/api/bookings/1/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        verify(bookingService, times(1)).updateStatus("1", "CONFIRMED");
    }

    // ----------------------- DELETE -----------------------
    @Test
    void deleteBooking_ReturnsNoContent() throws Exception {
        doNothing().when(bookingService).delete("1");

        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isNoContent());

        verify(bookingService, times(1)).delete("1");
    }
}
 