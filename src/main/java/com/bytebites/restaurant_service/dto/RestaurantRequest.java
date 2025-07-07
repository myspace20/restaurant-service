package com.bytebites.restaurant_service.dto;


import jakarta.validation.constraints.*;

public record RestaurantRequest(

        @NotBlank(message = "Name must not be blank")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Address must not be blank")
        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @NotBlank(message = "Phone number must not be blank")
        String phone,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Owner ID is required")
        Long ownerId
) {
}
