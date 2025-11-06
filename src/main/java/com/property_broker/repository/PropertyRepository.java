package com.property_broker.repository;

import com.property_broker.entity.Property;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
    List<Property> findByCity(String city);
    List<Property> findByStatus(String Status);
}
