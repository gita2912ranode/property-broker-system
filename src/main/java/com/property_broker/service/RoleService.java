package com.property_broker.service;

import com.property_broker.dto.RoleDto;
import com.property_broker.entity.Role;

public interface RoleService {
    Role findById(String id);
    Role create(RoleDto role);
    Role update(String id, RoleDto roleUpdate);
    void delete(String id);
}
