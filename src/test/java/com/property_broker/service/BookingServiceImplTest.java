package com.property_broker.service;

import com.property_broker.dto.BookingDto;
import com.property_broker.entity.Booking;
import com.property_broker.entity.Property;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.BookingRepository;
import com.property_broker.repository.PropertyRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.impl.BookingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
 
import org.mockito.InjectMocks;
import org.mockito.Mock;
 
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
 
import org.modelmapper.ModelMapper;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
 
import java.time.LocalDateTime;
import java.util.*;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // ⭐ FIX STRICT STUBBING ERRORS
class BookingServiceImplTest {
 
    @Mock private BookingRepository repo;
    @Mock private PropertyRepository propertyRepo;
    @Mock private UserRepository userRepo;
    @Mock private ModelMapper modelMapper;
 
    @InjectMocks
    private BookingServiceImpl service;
 
    private User loggedInUser;
    private Property property;
    private Booking booking;
    private BookingDto dto;
 
    @BeforeEach
    void setup() {
 
        // Mock Security Context
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john");
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);
 
        // Logged-in user
        loggedInUser = new User();
        loggedInUser.setId("cust1");
        loggedInUser.setUsername("john");
 
        // Property
        property = new Property();
        property.setId("p1");
 
        // Booking Entity
        booking = Booking.builder()
                .id("b1")
                .property(property)
                .customer(loggedInUser)
                .scheduledAt(LocalDateTime.now())
                .status("REQUESTED")
                .notes("test notes")
                .build();
 
        // Booking DTO
        dto = new BookingDto();
        dto.setScheduledAt(LocalDateTime.now());
        dto.setStatus("REQUESTED");
        dto.setNotes("Test booking");
    }
 
    // ----------------------------------------------------
    @Test
    void testFindAll() {
        when(repo.findAll()).thenReturn(List.of(booking));
 
        List<Booking> result = service.findAll();
 
        assertEquals(1, result.size());
        verify(repo).findAll();
    }
 
    // ----------------------------------------------------
    @Test
    void testFindByIdSuccess() {
        when(repo.findById("b1")).thenReturn(Optional.of(booking));
 
        Booking found = service.findById("b1");
 
        assertNotNull(found);
        assertEquals("b1", found.getId());
    }
 
    @Test
    void testFindByIdNotFound() {
        when(repo.findById("b1")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.findById("b1"));
    }
 
    // ----------------------------------------------------
    @Test
    void testCreateSuccess() {
        Booking mapped = new Booking();
 
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(loggedInUser));
        when(propertyRepo.findById("p1")).thenReturn(Optional.of(property));
        when(userRepo.findById("cust1")).thenReturn(Optional.of(loggedInUser));
        when(modelMapper.map(dto, Booking.class)).thenReturn(mapped);
        when(repo.save(mapped)).thenReturn(mapped);
 
        Booking created = service.create("p1", dto);
 
        assertNotNull(created);
        verify(repo).save(mapped);
    }
 
    @Test
    void testCreatePropertyNotFound() {
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(loggedInUser));
        when(propertyRepo.findById("p1")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create("p1", dto));
    }
 
    @Test
    void testCreateCustomerNotFound() {
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(loggedInUser));
        when(propertyRepo.findById("p1")).thenReturn(Optional.of(property));
        when(userRepo.findById("cust1")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create("p1", dto));
    }
 
    // ----------------------------------------------------
    @Test
    void testUpdateStatusSuccess() {
        when(repo.findById("b1")).thenReturn(Optional.of(booking));
        when(repo.save(booking)).thenReturn(booking);
 
        Booking updated = service.updateStatus("b1", "CONFIRMED");
 
        assertEquals("CONFIRMED", updated.getStatus());
    }
 
    // ----------------------------------------------------
    @Test
    void testDeleteSuccess() {
        when(repo.existsById("b1")).thenReturn(true);
 
        service.delete("b1");
 
        verify(repo).deleteById("b1");
    }
 
    @Test
    void testDeleteNotFound() {
        when(repo.existsById("b1")).thenReturn(false);
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete("b1"));
    }
 
    // ----------------------------------------------------
    @Test
    void testFindByProperty() {
        when(repo.findByPropertyId("p1")).thenReturn(List.of(booking));
 
        List<Booking> result = service.findByProperty("p1");
 
        assertEquals(1, result.size());
    }
 
    // ----------------------------------------------------
    @Test
    void testFindByCustomer() {
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(loggedInUser));
        when(repo.findByCustomerId("cust1")).thenReturn(List.of(booking));
 
        List<Booking> result = service.findByCustomer();
 
        assertEquals(1, result.size());
        assertEquals("b1", result.get(0).getId());
    }
}
 