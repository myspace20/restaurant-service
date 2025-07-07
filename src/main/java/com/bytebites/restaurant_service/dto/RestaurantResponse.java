package com.bytebites.restaurant_service.dto;

import java.util.List;

public record RestaurantResponse(
        Long id,
        String name,
        String address,
        String phone,
        String email,
        Long ownerId,
        List<MenuResponse> menus
) {
}
