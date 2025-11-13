package com.property_broker.service;



import com.property_broker.dto.RoleDto;
import com.property_broker.entity.Role;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.RoleRepository;
import com.property_broker.service.impl.RoleServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
 
import org.modelmapper.ModelMapper;
 
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
 
    @Mock
    private RoleRepository repo;
 
    @Mock
    private ModelMapper modelMapper;
 
    @InjectMocks
    private RoleServiceImpl service;
 
    private Role role;
    private RoleDto dto;
 
    @BeforeEach
    void setup() {
        role = new Role();
        role.setId("111");
        role.setName("ADMIN");
 
        dto = new RoleDto();
        dto.setName("ADMIN");
    }
 
    // ✔️ Test: findAll()
    @Test
    void testFindAll() {
        when(repo.findAll()).thenReturn(List.of(role));
 
        List<Role> result = service.findAll();
 
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        verify(repo, times(1)).findAll();
    }
 
    // ✔️ Test: findById() - success
    @Test
    void testFindByIdSuccess() {
        when(repo.findById("111")).thenReturn(Optional.of(role));
 
        Role found = service.findById("111");
 
        assertEquals("ADMIN", found.getName());
    }
 
    // ✔️ Test: findById() - NOT FOUND
    @Test
    void testFindByIdNotFound() {
        when(repo.findById("111")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class, () -> service.findById("111"));
    }
 
    // ✔️ Test: create()
    @Test
    void testCreateRole() {
        Role mapped = new Role();
        mapped.setName("ADMIN");
 
        when(modelMapper.map(dto, Role.class)).thenReturn(mapped);
        when(repo.save(mapped)).thenReturn(role);
 
        Role created = service.create(dto);
 
        assertEquals("ADMIN", created.getName());
        verify(repo).save(mapped);
    }
 
    // ✔️ Test: update() - success
    @Test
    void testUpdateSuccess() {
        Role mapped = new Role();
        mapped.setName("UPDATED");
 
        Role existing = new Role("111", "ADMIN");
 
        when(repo.findById("111")).thenReturn(Optional.of(existing));
        when(modelMapper.map(dto, Role.class)).thenReturn(mapped);
        when(repo.save(existing)).thenReturn(existing);
 
        Role result = service.update("111", dto);
 
        assertEquals("ADMIN", existing.getName());   // existing updated based on dto
        verify(repo).save(existing);
    }
 
    // ✔️ Test: update() - NOT FOUND
    @Test
    void testUpdateNotFound() {
        when(repo.findById("111")).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update("111", dto));
    }
 
    // ✔️ Test: delete() - success
    @Test
    void testDeleteSuccess() {
        when(repo.existsById("111")).thenReturn(true);
 
        service.delete("111");
 
        verify(repo).deleteById("111");
    }
 
    // ✔️ Test: delete() - NOT FOUND
    @Test
    void testDeleteNotFound() {
        when(repo.existsById("111")).thenReturn(false);
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete("111"));
    }
}
 