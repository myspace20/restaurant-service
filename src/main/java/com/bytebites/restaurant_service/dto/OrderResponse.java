package com.bytebites.restaurant_service.dto;

public record OrderResponse(
        Long restaurantId,
        Long userId,
        Long menuId,
        String status
) {
}