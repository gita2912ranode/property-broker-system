package com.property_broker.service.impl;

import com.property_broker.entity.Offer;
import com.property_broker.entity.Property;
import com.property_broker.entity.User;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.OfferRepository;
import com.property_broker.repository.PropertyRepository;
import com.property_broker.repository.UserRepository;
import com.property_broker.service.OfferService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository repo;
    private final PropertyRepository propertyRepo;
    private final UserRepository userRepo;

    public OfferServiceImpl(OfferRepository repo, PropertyRepository propertyRepo, UserRepository userRepo) {
        this.repo = repo;
        this.propertyRepo = propertyRepo;
        this.userRepo = userRepo;
    }

    public List<Offer> findAll() {
        return repo.findAll();
    }

    public Offer findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer not found: " + id));
    }

    @Transactional
    public Offer create(String propertyId, String customerId, Offer offer) {
        Property property = propertyRepo.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
        User customer = userRepo.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        offer.setProperty(property);
        offer.setCustomer(customer);
        if (offer.getStatus() == null) offer.setStatus("PENDING");
        return repo.save(offer);
    }

    @Transactional
    public Offer updateStatus(String id, String status) {
        Offer existing = findById(id);
        existing.setStatus(status);
        return repo.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Offer not found: " + id);
        repo.deleteById(id);
    }

    public List<Offer> findByProperty(String propertyId) {
        return repo.findByPropertyId(propertyId);
    }

    public List<Offer> findByCustomer(String customerId) {
        return repo.findByCustomerId(customerId);
    }
}
