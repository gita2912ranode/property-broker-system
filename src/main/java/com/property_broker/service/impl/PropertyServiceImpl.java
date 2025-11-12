package com.property_broker.service.impl;

import com.property_broker.dto.PropertyDto;
import com.property_broker.entity.Property;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.PropertyRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.PropertyService;
import com.property_broker.specification.PropertySpecification;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository repo;
    private final UserRepository userRepo;

    private final ModelMapper modelMapper;

    public PropertyServiceImpl(PropertyRepository repo, UserRepository userRepo, ModelMapper modelMapper) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }
    
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public List<Property> findAll() {
        return repo.findAll();
    }

    public Property findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));
    }
    

    @Transactional
    public Property create(PropertyDto propertydto) {
    	User loggedInUser=getCurrentUser();
    	String currentUserId=loggedInUser.getId();
    	
    	
        Property property=modelMapper.map(propertydto,Property.class);
        if (currentUserId != null) {
            User owner = userRepo.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + currentUserId));
            property.setOwner(owner);
        }
        return repo.save(property);
    }

    @Transactional
    public Property update(String id, PropertyDto propertyDto) {
        Property existing = findById(id);
        User currentUser = getCurrentUser();
 
        if (!existing.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not authorized to update this property.");
        }
 
        existing.setTitle(propertyDto.getTitle());
        existing.setDescription(propertyDto.getDescription());
        existing.setPropertyType(propertyDto.getPropertyType());
        existing.setPrice(propertyDto.getPrice());
        existing.setAddress(propertyDto.getAddress());
        existing.setCity(propertyDto.getCity());
        existing.setState(propertyDto.getState());
        existing.setCountry(propertyDto.getCountry());
        existing.setZipcode(propertyDto.getZipcode());
        existing.setBedrooms(propertyDto.getBedrooms());
        existing.setBathrooms(propertyDto.getBathrooms());
        existing.setAreaSqft(propertyDto.getAreaSqft());
        existing.setImageUrl(propertyDto.getImageUrl());
        existing.setUpdatedAt(Instant.now());
 
        return repo.save(existing);
    }

    /**
     * Delete property — only allowed for the property owner.
     */
    @Transactional
    public void delete(String id) {
        Property existing = findById(id);
        User currentUser = getCurrentUser();
 
        if (!existing.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not authorized to delete this property.");
        }
 
        repo.delete(existing);
    }

    public List<Property> searchProperties(
            String type, Double minPrice, Double maxPrice,
            String city, String state, Integer bedrooms, Integer bathrooms,
            String sortBy, String direction
    ) {
        Specification<Property> spec = buildSpecification(type, minPrice, maxPrice, city, state, bedrooms, bathrooms);
        Sort sort = Sort.by(Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC),
                sortBy != null ? sortBy : "price");
        return repo.findAll(spec, sort);
    }

    public Page<Property> searchPropertiesPaginated(
            String type, Double minPrice, Double maxPrice,
            String city, String state, Integer bedrooms, Integer bathrooms,
            Pageable pageable
    ) {
        Specification<Property> spec = buildSpecification(type, minPrice, maxPrice, city, state, bedrooms, bathrooms);
        return repo.findAll(spec, pageable);
    }

    private Specification<Property> buildSpecification(String type, Double minPrice, Double maxPrice,
                                                       String city, String state, Integer bedrooms, Integer bathrooms) {
        return Stream.of(
                        PropertySpecification.hasType(type),
                        PropertySpecification.hasCity(city),
                        PropertySpecification.hasState(state),
                        PropertySpecification.priceBetween(minPrice, maxPrice),
                        PropertySpecification.hasBedrooms(bedrooms),
                        PropertySpecification.hasBathrooms(bathrooms)
                )
                .filter(Objects::nonNull)
                .reduce((s1, s2) -> s1.and(s2))
                .orElse((root, query, cb) -> cb.conjunction());
    }
    
    @Override
    public List<Property> findByOwnerId() {
    	
    	User owner=getCurrentUser();
    	String ownerId=owner.getId();
        List<Property> properties =repo.findByOwner_Id(ownerId);
        if (properties.isEmpty()) {
            throw new ResourceNotFoundException("No properties found for owner ID: " + ownerId);
        }
        return properties;
    }

}
