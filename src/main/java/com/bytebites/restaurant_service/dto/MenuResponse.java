package com.bytebites.restaurant_service.dto;


public record MenuResponse(
        Long id,
        String name,
        String description,
        Double price
) {
}
