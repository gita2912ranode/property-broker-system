package com.property_broker.service;

import com.property_broker.entity.Property;

import java.util.List;

public interface PropertyService{
        List<Property> findAll();
        Property create(Property property, String ownerId);
        Property update(String id, Property payload);
        void delete(String id);
}
