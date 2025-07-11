package com.bytebites.restaurant_service.services;

import com.bytebites.restaurant_service.models.Menu;
import com.bytebites.restaurant_service.models.Restaurant;
import com.bytebites.restaurant_service.repositories.MenuRepository;
import com.bytebites.restaurant_service.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("ownershipService")
public class RestaurantOwnershipServiceImpl implements RestaurantOwnershipService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    private final Logger logger = LoggerFactory.getLogger(RestaurantOwnershipServiceImpl.class);

    public RestaurantOwnershipServiceImpl(RestaurantRepository restaurantRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public boolean checkRestaurantOwnership(Long restaurantId, Long ownerId ) {
        logger.info("Checking restaurant ownership for ownerId={}, restaurantId={}", ownerId, restaurantId);
        return restaurantRepository.existsByIdAndOwnerId(restaurantId, ownerId);
    }

    @Override
    public boolean checkMenuOwnership(Long menuId, Long ownerId) {
        logger.info("Checking menu ownership for ownerId={}, menuId={}", ownerId, menuId);
        return menuRepository.findById(menuId)
                .map(Menu::getRestaurant)
                .map(Restaurant::getOwnerId)
                .map(owner_Id -> owner_Id.equals(ownerId))
                .orElse(false);
    }
}
