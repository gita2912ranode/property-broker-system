package com.property_broker.service;

import com.property_broker.entity.User;
import java.util.List;


public interface UserService {
    List<User> findAll();
    User findById(String id);
    User create(User user);
    User update(String id,User user);
    void delete(String id);
    User assignRole(String userId,String roleName);
    User removeRole(String userId,String roleName);
}
