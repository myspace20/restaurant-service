package com.bytebites.restaurant_service.services;


import com.bytebites.restaurant_service.dto.RestaurantRequest;
import com.bytebites.restaurant_service.dto.RestaurantResponse;
import com.bytebites.restaurant_service.exceptions.ResourceNotFound;
import com.bytebites.restaurant_service.mappers.RestaurantMapper;
import com.bytebites.restaurant_service.models.Restaurant;
import com.bytebites.restaurant_service.models.Menu;
import com.bytebites.restaurant_service.repositories.MenuRepository;
import com.bytebites.restaurant_service.repositories.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final RestaurantMapper restaurantMapper;

    private final Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);


    public RestaurantServiceImpl(MenuRepository menuRepository, RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public Restaurant getRestaurantById(Long id){
        logger.info("Getting restaurant by id {} ", id);
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Restaurant not found"));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<RestaurantResponse> getAllRestaurants() {
        logger.info("Getting all restaurants ");
       return restaurantRepository.findAll().stream()
               .map(restaurantMapper::toRestaurantResponse)
               .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public RestaurantResponse getRestaurantByName(String restaurantName) {
        logger.info("Getting restaurant by name {} ", restaurantName);
        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(()->new ResourceNotFound("Restaurant not found"));
        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    public RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest) {
        logger.info("Creating restaurant with {}", restaurantRequest);
        Restaurant restaurant = new Restaurant(
                restaurantRequest.name()
                ,restaurantRequest.address(),
                restaurantRequest.phone(),
                restaurantRequest.email(),
                restaurantRequest.ownerId()
        );
        Restaurant newRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toRestaurantResponse(newRestaurant);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and @ownershipService.checkRestaurantOwnership(#id, authentication.principal.id)")
    public void updateRestaurant(Long id, RestaurantRequest restaurantRequest) {
        logger.info("Updating restaurant with id {} and requestBody {}", id, restaurantRequest);
        Restaurant restaurantToUpdate = getRestaurantById(id);
        Restaurant updatedRestaurant = setRestaurantToUpdateProperties(
                restaurantToUpdate,
                restaurantRequest
        );
        restaurantRepository.save(updatedRestaurant);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and @ownershipService.checkRestaurantOwnership(#id, authentication.principal.id)")
    public void deleteRestaurant(Long id) {
        logger.info("Deleting restaurant with id {} ", id);
        Restaurant  restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and @ownershipService.checkRestaurantOwnership(#restaurantId, authentication.principal.id)")
    public void removeMenuFromRestaurant(Long restaurantId, Long menuId) {
        logger.info("Removing menu from restaurant with id {} and menu id {}", restaurantId, menuId);
        Restaurant restaurant = getRestaurantById(restaurantId);
        Menu menu = getMenuById(menuId);
        restaurant.removeMenu(menu);
        restaurantRepository.save(restaurant);
    }

    @Override
    public RestaurantResponse getRestaurantByOwnerId(Long ownerId) {
        logger.info("Getting restaurant by owner id {} ", ownerId);
        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFound("Restaurant not found"));
        return restaurantMapper.toRestaurantResponse(restaurant);
    }


    private Menu getMenuById(Long menuId) {
        return  menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFound("Menu not found"));
    }

    private Restaurant setRestaurantToUpdateProperties(Restaurant restaurantToUpdate, RestaurantRequest restaurantRequest) {
        restaurantToUpdate.setName(restaurantRequest.name());
        restaurantToUpdate.setAddress(restaurantRequest.address());
        restaurantToUpdate.setPhone(restaurantRequest.phone());
        restaurantToUpdate.setEmail(restaurantRequest.email());
        return restaurantToUpdate;
    }
}
