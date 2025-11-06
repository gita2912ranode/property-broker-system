package com.property_broker.service;

import com.property_broker.entity.Role;

public interface RoleService {
    Role findById(String id);
    Role create(Role role);
    Role update(String id, Role roleUpdate);
    void delete(String id);
}
