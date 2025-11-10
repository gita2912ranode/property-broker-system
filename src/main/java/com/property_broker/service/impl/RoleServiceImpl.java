package com.property_broker.service.impl;

import com.property_broker.dto.RoleDto;
import com.property_broker.entity.Role;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.RoleRepository;
import com.property_broker.service.RoleService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repo;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository repo, ModelMapper modelMapper) {
        this.repo = repo;
        this.modelMapper = modelMapper;
    }

    public List<Role> findAll() {
        return repo.findAll();
    }

    public Role findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }

    @Transactional
    public Role create(RoleDto roleDto) {
        Role role=modelMapper.map(roleDto,Role.class);
        return repo.save(role);
    }

    @Transactional
    public Role update(String id, RoleDto roleUpdate) {
        Role role= modelMapper.map(roleUpdate,Role.class);
        Role existing = findById(id);
        existing.setName(roleUpdate.getName());
        return repo.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Role not found: " + id);
        repo.deleteById(id);
    }
}
