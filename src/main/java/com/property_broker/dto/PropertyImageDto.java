package com.property_broker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PropertyImageDto {
    private String id;

    @NotBlank(message = "property id is required")
    private String propertyId;

    @NotBlank(message = "url is required")
    private String url;

    @NotBlank(message = "caption is required")
    private String caption;



}
