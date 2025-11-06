package com.property_broker.service.impl;

import com.property_broker.entity.Role;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.RoleRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;


    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findById(String id) {
        return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Transactional
    public User create(User user) {
        // hash password if provided

        if (user.getRoles() == null) {
            // assign default role if needed (e.g., ROLE_CUSTOMER)
            roleRepo.findByName("CUSTOMER").ifPresent(role -> user.setRoles(Set.of(role)));
        }
        return userRepo.save(user);
    }

    @Transactional
    public User update(String id, User data) {
        User existing = findById(id);
        existing.setFirstName(data.getFirstName());
        existing.setLastName(data.getLastName());
        existing.setPhone(data.getPhone());
        existing.setEmail(data.getEmail());
        // update username/password carefully

        if (data.getRoles() != null) {
            // optionally resolve roles from DB to attach managed entities
            Set<Role> resolved = new HashSet<>();
            for (Role r : data.getRoles()) {
                roleRepo.findByName(r.getName()).ifPresent(resolved::add);
            }
            if (!resolved.isEmpty()) existing.setRoles(resolved);
        }
        return userRepo.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!userRepo.existsById(id)) throw new ResourceNotFoundException("User not found: " + id);
        userRepo.deleteById(id);
    }

    @Transactional
    public User assignRole(String userId, String roleName) {
        User user = findById(userId);
        Role role = roleRepo.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
        Set<Role> roles = user.getRoles() != null ? new HashSet<>(user.getRoles()) : new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepo.save(user);
    }

    @Transactional
    public User removeRole(String userId, String roleName) {
        User user = findById(userId);
        if (user.getRoles() == null) return user;
        user.getRoles().removeIf(r -> r.getName().equals(roleName));
        return userRepo.save(user);
    }
}
