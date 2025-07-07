package com.bytebites.restaurant_service.services;

import com.bytebites.restaurant_service.dto.RestaurantRequest;
import com.bytebites.restaurant_service.dto.RestaurantResponse;
import com.bytebites.restaurant_service.models.Restaurant;

import java.util.List;

public interface RestaurantService {
    Restaurant getRestaurantById(Long id);
    List<RestaurantResponse> getAllRestaurants();
    RestaurantResponse getRestaurantByName(String restaurantName);
    RestaurantResponse createRestaurant(RestaurantRequest restaurant);
    void updateRestaurant(Long id, RestaurantRequest restaurant);
    void deleteRestaurant(Long id);
    void removeMenuFromRestaurant(Long restaurantId, Long menuId);
    RestaurantResponse getRestaurantByOwnerId(Long ownerId);
}
