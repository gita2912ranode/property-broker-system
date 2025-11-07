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
import org.springframework.stereotype.Service;
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

    public List<Property> findAll() {
        return repo.findAll();
    }

    public Property findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));
    }

    @Transactional
    public Property create(PropertyDto propertydto, String ownerId) {
        Property property=modelMapper.map(propertydto,Property.class);
        if (ownerId != null) {
            User owner = userRepo.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerId));
            property.setOwner(owner);
        }
        return repo.save(property);
    }

    @Transactional
    public Property update(String id, PropertyDto propertyDto) {

        Property payload=modelMapper.map(propertyDto,Property.class);
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

}
