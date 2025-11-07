package com.property_broker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDto {
    private String id;

    @NotBlank(message = "name is required")
    private String name;

}
