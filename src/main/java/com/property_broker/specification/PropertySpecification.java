package com.property_broker.specification;


import com.property_broker.entity.Property;
import org.springframework.data.jpa.domain.Specification;

public class PropertySpecification {

    public static Specification<Property> hasType(String type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Property> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%");
    }

    public static Specification<Property> hasState(String state) {
        return (root, query, cb) ->
                state == null ? null : cb.like(cb.lower(root.get("state")), "%" + state.toLowerCase() + "%");
    }

    public static Specification<Property> priceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("price"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("price"), max);
            }
            return null;
        };
    }

    public static Specification<Property> hasBedrooms(Integer bedrooms) {
        return (root, query, cb) ->
                bedrooms == null ? null : cb.equal(root.get("bedrooms"), bedrooms);
    }

    public static Specification<Property> hasBathrooms(Integer bathrooms) {
        return (root, query, cb) ->
                bathrooms == null ? null : cb.equal(root.get("bathrooms"), bathrooms);
    }
}
