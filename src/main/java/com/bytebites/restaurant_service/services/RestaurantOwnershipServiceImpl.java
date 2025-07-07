package com.bytebites.restaurant_service.services;

import com.bytebites.restaurant_service.repositories.RestaurantRepository;
import org.springframework.stereotype.Component;

@Component("ownershipService")
public class RestaurantOwnershipServiceImpl implements RestaurantOwnershipService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantOwnershipServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public boolean checkRestaurantOwnership(Long ownerId) {
        return restaurantRepository.existsByOwnerId(ownerId);
    }
}
