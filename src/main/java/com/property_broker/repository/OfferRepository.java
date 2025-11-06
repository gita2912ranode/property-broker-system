package com.property_broker.repository;

import com.property_broker.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface OfferRepository extends JpaRepository<Offer, String> {
    List<Offer> findByPropertyId(String propertyId);
    List<Offer> findByCustomerId(String customerId);

}
