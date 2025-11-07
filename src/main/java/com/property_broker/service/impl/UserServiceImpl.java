package com.property_broker.service.impl;

import com.property_broker.dto.UserDto;
import com.property_broker.entity.Role;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.RoleRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
 
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    private final ModelMapper modelMapper;


    
    
    @Lazy
    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder,
			ModelMapper modelMapper) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.encoder = encoder;
		this.modelMapper = modelMapper;
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
    public User create(UserDto userdto) {

        User user=modelMapper.map(userdto,User.class);

        if (user.getRoles() == null) {
            // assign default role if needed (e.g., ROLE_CUSTOMER)
            roleRepo.findByName("CUSTOMER").ifPresent(role -> user.setRoles(Set.of(role)));
        }
        
        user.setPassword(encoder.encode(user.getPassword())); 
        return userRepo.save(user);
    }

    @Transactional
    public User update(String id, UserDto updatedUser) {
        User data=modelMapper.map(updatedUser,User.class);
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

//	@Override
//	public UserDetails loadUserByUserName(String username) {
//        com.property_broker.entity.User u = userRepo.findByUsername(username.toLowerCase())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        String[] roleNames = u.getRoles().stream()
//                .map(Role::getName)
//                .toArray(String[]::new);
//
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(u.getUsername())
//                .password(u.getPassword())
//                .roles(roleNames)
//                .build(); 
//	}

	@Override
    public boolean checkUserCredentails(String username, String password) {
        Optional<User> optionalUser = userRepo.findByUsername(username);

        if (optionalUser.isEmpty()) { 
            return false;
        }
 
        User user = optionalUser.get();

        return encoder.matches(password, user.getPassword());
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.property_broker.entity.User u = userRepo.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String[] roleNames = u.getRoles().stream()
                .map(Role::getName)
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .roles(roleNames)
                .build(); 
	}
 
	 
}
