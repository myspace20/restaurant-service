package com.bytebites.restaurant_service.services;

public interface RestaurantOwnershipService {
    boolean checkRestaurantOwnership(Long ownerId, Long restaurantId);
    boolean checkMenuOwnership(Long ownerId, Long menuId);
    }
