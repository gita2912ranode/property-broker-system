package com.property_broker.repository;

import com.property_broker.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String>, JpaSpecificationExecutor<Property> {
    List<Property> findByCity(String city);
    List<Property> findByStatus(String Status);
    List<Property> findByOwner_Id(String ownerId);
}
