package com.bytebites.restaurant_service.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Description must not be blank")
        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        Double price,

        @NotNull(message = "Restaurant id is required")
        Long restaurant_id
) {
}
