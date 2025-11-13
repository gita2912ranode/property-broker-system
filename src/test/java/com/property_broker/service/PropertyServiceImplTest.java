package com.property_broker.service;

import com.property_broker.dto.PropertyDto;
import com.property_broker.entity.Property;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.PropertyRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.impl.PropertyServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
 
import org.mockito.InjectMocks;
import org.mockito.Mock;
 
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
 
import org.modelmapper.ModelMapper;
 
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
 
import java.time.Instant;
import java.util.*;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // ⭐ FIXES unnecessary stubbings
class PropertyServiceImplTest {
 
    @Mock private PropertyRepository repo;
    @Mock private UserRepository userRepo;
    @Mock private ModelMapper modelMapper;
 
    @InjectMocks
    private PropertyServiceImpl service;
 
    private User user;
    private Property property;
    private PropertyDto dto;
 
    @BeforeEach
    void setup() {
 
        // Mock Security Context
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
 
        when(userDetails.getUsername()).thenReturn("john");
        when(auth.getPrincipal()).thenReturn(userDetails);
 
        SecurityContextHolder.getContext().setAuthentication(auth);
 
        // Prepare user
        user = new User();
        user.setId("owner1");
        user.setUsername("john");
 
        // Prepare property
        property = new Property();
        property.setId("p1");
        property.setOwner(user);
        property.setTitle("Test Property");
        property.setCity("Mumbai");
        property.setCreatedAt(Instant.now());
 
        // Prepare DTO
        dto = new PropertyDto();
        dto.setTitle("Updated");
        dto.setDescription("Desc");
        dto.setPropertyType("Flat");
        dto.setPrice(2000.0);
        dto.setCity("Mumbai");
        dto.setState("MH");
        dto.setCountry("India");
        dto.setZipcode("400001");
        dto.setBedrooms(2);
        dto.setBathrooms(2);
        dto.setAreaSqft(900);
        dto.setImageUrl("image.jpg");
    }
 
    // ---------------------------------------------------------------------
    @Test
    void testFindAll() {
        when(repo.findAll()).thenReturn(List.of(property));
 
        List<Property> list = service.findAll();
 
        assertEquals(1, list.size());
    }
 
    // ---------------------------------------------------------------------
    @Test
    void testFindByIdSuccess() {
        when(repo.findById("p1")).thenReturn(Optional.of(property));
 
        Property result = service.findById("p1");
 
        assertEquals("Test Property", result.getTitle());
    }
 
    @Test
    void testFindByIdNotFound() {
        when(repo.findById("p1")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.findById("p1"));
    }
 
    // ---------------------------------------------------------------------
    @Test
    void testCreateSuccess() {
 
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepo.findById("owner1")).thenReturn(Optional.of(user));
 
        when(modelMapper.map(dto, Property.class)).thenReturn(property);
        when(repo.save(property)).thenReturn(property);
 
        Property created = service.create(dto);
 
        assertNotNull(created);
        verify(repo).save(property);
    }
 
    // ---------------------------------------------------------------------
    @Test
    void testUpdateSuccess() {
        when(repo.findById("p1")).thenReturn(Optional.of(property));
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(repo.save(property)).thenReturn(property);
 
        Property updated = service.update("p1", dto);
 
        assertEquals("Updated", updated.getTitle());
    }
 
    @Test
    void testUpdateUnauthorized() {
 
        User anotherOwner = new User();
        anotherOwner.setId("other-owner");
 
        property.setOwner(anotherOwner);
 
        when(repo.findById("p1")).thenReturn(Optional.of(property));
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
 
        assertThrows(SecurityException.class,
                () -> service.update("p1", dto));
    }
 
    @Test
    void testUpdateNotFound() {
        when(repo.findById("p1")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update("p1", dto));
    }
 
    // ---------------------------------------------------------------------
    @Test
    void testDeleteSuccess() {
        when(repo.findById("p1")).thenReturn(Optional.of(property));
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
 
        service.delete("p1");
 
        verify(repo).delete(property);
    }
 
    @Test
    void testDeleteUnauthorized() {
 
        User another = new User();
        another.setId("other-id");
 
        property.setOwner(another);
 
        when(repo.findById("p1")).thenReturn(Optional.of(property));
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
 
        assertThrows(SecurityException.class,
                () -> service.delete("p1"));
    }
 
    @Test
    void testDeleteNotFound() {
        when(repo.findById("p1")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete("p1"));
    }
 
    // ---------------------------------------------------------------------
    @Test
    void testFindByOwnerIdSuccess() {
 
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(repo.findByOwner_Id("owner1")).thenReturn(List.of(property));
 
        List<Property> list = service.findByOwnerId();
 
        assertEquals(1, list.size());
    }
 
    @Test
    void testFindByOwnerIdEmpty() {
 
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(repo.findByOwner_Id("owner1")).thenReturn(List.of());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.findByOwnerId());
    }
 
    // ---------------------------------------------------------------------
    @Test
    void testSearchProperties() {
 
        when(repo.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(property));
 
        List<Property> result = service.searchProperties(
                "flat", 100.0, 200.0, "Mumbai",
                "MH", 1, 1,
                "price", "asc"
        );
 
        assertEquals(1, result.size());
    }
 
    @Test
    void testSearchPropertiesPaginated() {
 
        Page<Property> page = new PageImpl<>(List.of(property));
 
        when(repo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
 
        Page<Property> result = service.searchPropertiesPaginated(
                "flat", 100.0, 200.0,
                "Mumbai", "MH",
                1, 1,
                PageRequest.of(0, 10)
        );
 
        assertEquals(1, result.getContent().size());
    }
}