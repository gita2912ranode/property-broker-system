package com.property_broker.service.impl;

import com.property_broker.entity.Offer;
import com.property_broker.entity.Transaction;
import com.property_broker.exception.ResourceNotFoundException;
import com.property_broker.repository.OfferRepository;
import com.property_broker.repository.TransactionRepository;
import com.property_broker.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repo;
    private final OfferRepository offerRepo;

    public TransactionServiceImpl(TransactionRepository repo, OfferRepository offerRepo) {
        this.repo = repo;
        this.offerRepo = offerRepo;
    }

    public List<Transaction> findAll() {
        return repo.findAll();
    }

    public Transaction findById(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
    }

    @Transactional
    public Transaction create(String offerId, Transaction transaction) {
        Offer offer = offerRepo.findById(offerId).orElseThrow(() -> new ResourceNotFoundException("Offer not found: " + offerId));
        transaction.setOffer(offer);
        return repo.save(transaction);
    }

    @Transactional
    public void delete(String id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Transaction not found: " + id);
        repo.deleteById(id);
    }
}
