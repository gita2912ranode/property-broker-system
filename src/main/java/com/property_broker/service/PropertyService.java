package com.property_broker.service;

import com.property_broker.dto.PropertyDto;
import com.property_broker.dto.PropertyResponseDto;
import com.property_broker.entity.Property;

import java.util.List;

public interface PropertyService{
        List<Property> findAll();
        Property create(PropertyDto property, String ownerId);
        Property update(String id, PropertyDto payload);
        void delete(String id);

        List<Property> searchProperties(String type, Double minPrice, Double maxPrice,
                                        String city, String state, Integer bedrooms, Integer bathrooms,
                                        String sortBy, String direction);
}
