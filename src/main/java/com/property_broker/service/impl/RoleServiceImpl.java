package com.property_broker.service.impl;

import com.property_broker.entity.Role;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.RoleRepository;
import com.property_broker.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repo;

    public RoleServiceImpl(RoleRepository repo) {
        this.repo = repo;
    }

    public List<Role> findAll() {
        return repo.findAll();
    }

    public Role findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }

    @Transactional
    public Role create(Role role) {
        return repo.save(role);
    }

    @Transactional
    public Role update(String id, Role roleUpdate) {
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
