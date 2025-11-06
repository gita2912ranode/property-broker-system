package com.property_broker.service;

import com.property_broker.entity.Offer;

import java.util.List;

public interface OfferService {
    List<Offer> findAll();
    Offer findById(String id);
    Offer create(String propertyId, String customerId, Offer offer);
    Offer updateStatus(String id, String status);
    void delete(String id);



}
