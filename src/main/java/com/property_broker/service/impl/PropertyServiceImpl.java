package com.property_broker.service.impl;

import com.property_broker.entity.Property;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.PropertyRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.PropertyService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository repo;
    private final UserRepository userRepo;

    public PropertyServiceImpl(PropertyRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public List<Property> findAll() {
        return repo.findAll();
    }

    public Property findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));
    }

    @Transactional
    public Property create(Property property, String ownerId) {
        if (ownerId != null) {
            User owner = userRepo.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerId));
            property.setOwner(owner);
        }
        return repo.save(property);
    }

    @Transactional
    public Property update(String id, Property payload) {
        Property existing = findById(id);
        // update fields
        existing.setTitle(payload.getTitle());
        existing.setDescription(payload.getDescription());
        existing.setPropertyType(payload.getPropertyType());
        existing.setPrice(payload.getPrice());
        existing.setAddress(payload.getAddress());
        existing.setCity(payload.getCity());
        existing.setState(payload.getState());
        existing.setCountry(payload.getCountry());
        existing.setZipcode(payload.getZipcode());
        existing.setBedrooms(payload.getBedrooms());
        existing.setBathrooms(payload.getBathrooms());
        existing.setAreaSqft(payload.getAreaSqft());
        existing.setStatus(payload.getStatus());
        return repo.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Property not found: " + id);
        repo.deleteById(id);
    }

}
