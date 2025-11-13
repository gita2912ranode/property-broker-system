package com.property_broker.service;

import com.property_broker.dto.UserDto;
import com.property_broker.entity.Role;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.RoleRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
 
import java.util.*;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
class UserServiceImplTest {
 
    @Mock
    private UserRepository userRepo;
 
    @Mock
    private RoleRepository roleRepo;
 
    @Mock
    private PasswordEncoder encoder;
 
    @Mock
    private ModelMapper modelMapper;
 
    @InjectMocks
    private UserServiceImpl service;
 
    private User user;
    private UserDto userDto;
    private Role role;
 
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
 
        user = new User();
        user.setId("1");
        user.setUsername("gitanjali");
        user.setEmail("git@example.com");
        user.setPassword("encodedPass");
 
        userDto = new UserDto();
        userDto.setUsername("gitanjali");
        userDto.setEmail("git@example.com");
        userDto.setPassword("12345678");
 
        role = new Role("1", "CUSTOMER");
    }
 
    @Test
    void testFindAll() {
        when(userRepo.findAll()).thenReturn(List.of(user));
 
        List<User> result = service.findAll();
 
        assertEquals(1, result.size());
        verify(userRepo, times(1)).findAll();
    }
 
    @Test
    void testFindByIdSuccess() {
        when(userRepo.findById("1")).thenReturn(Optional.of(user));
 
        User result = service.findById("1");
 
        assertEquals("gitanjali", result.getUsername());
    }
 
    @Test
    void testFindByIdNotFound() {
        when(userRepo.findById("10")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class, () -> service.findById("10"));
    }
 
    @Test
    void testCreateUserWithDefaultRole() {
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(roleRepo.findByName("CUSTOMER")).thenReturn(Optional.of(role));
        when(encoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepo.save(any(User.class))).thenReturn(user);
 
        User result = service.create(userDto);
 
        assertEquals("encodedPass", result.getPassword());
        assertTrue(result.getRoles().contains(role));
    }
 
    @Test
    void testUpdateUser() {
        User updated = new User();
        updated.setUsername("gitanjali");
        updated.setEmail("git@example.com");
 
        when(modelMapper.map(userDto, User.class)).thenReturn(updated);
        when(userRepo.findById("1")).thenReturn(Optional.of(user));
        when(roleRepo.findByName(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(user)).thenReturn(user);
 
        User result = service.update("1", userDto);
 
        assertEquals("git@example.com", result.getEmail());
    }
 
    @Test
    void testDeleteUserSuccess() {
        when(userRepo.existsById("1")).thenReturn(true);
 
        service.delete("1");
 
        verify(userRepo, times(1)).deleteById("1");
    }
 
    @Test
    void testDeleteUserNotFound() {
        when(userRepo.existsById("11")).thenReturn(false);
 
        assertThrows(ResourceNotFoundException.class, () -> service.delete("11"));
    }
 
    @Test
    void testAssignRole() {
        when(userRepo.findById("1")).thenReturn(Optional.of(user));
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(new Role("2", "ADMIN")));
        when(userRepo.save(user)).thenReturn(user);
 
        User result = service.assignRole("1", "ADMIN");
 
        assertTrue(result.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN")));
    }
 
    @Test
    void testAssignRoleNotFound() {
        when(userRepo.findById("1")).thenReturn(Optional.of(user));
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class, () -> service.assignRole("1", "ADMIN"));
    }
 
    @Test
    void testRemoveRole() {
        Role role = new Role("1", "ADMIN");
     
        // Use a mutable set
        user.setRoles(new HashSet<>(Set.of(role)));
     
        when(userRepo.findById("123")).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
     
        User updated = service.removeRole("123", "ADMIN");
     
        assertFalse(updated.getRoles().contains(role));
    }
    
 
    @Test
    void testLoadUserByUsernameSuccess() {
        user.setRoles(Set.of(role));
        when(userRepo.findByUsername("gitanjali")).thenReturn(Optional.of(user));
 
        UserDetails details = service.loadUserByUsername("gitanjali");
 
        assertEquals("gitanjali", details.getUsername());
        assertEquals(1, details.getAuthorities().size());
    }
 
    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepo.findByUsername("wrong")).thenReturn(Optional.empty());
 
        assertThrows(RuntimeException.class, () -> service.loadUserByUsername("wrong"));
    }
 
    @Test
    void testCheckCredentials() {
        when(userRepo.findByUsername("gitanjali")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "encodedPass")).thenReturn(true);
 
        boolean result = service.checkUserCredentails("gitanjali", "123");
 
        assertTrue(result);
    }
 
    @Test
    void testCheckCredentialsInvalidUser() {
        when(userRepo.findByUsername("wrong")).thenReturn(Optional.empty());
 
        assertFalse(service.checkUserCredentails("wrong", "123"));
    }
}