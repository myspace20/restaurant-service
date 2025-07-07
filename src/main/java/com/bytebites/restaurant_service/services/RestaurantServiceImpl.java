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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImpl(MenuRepository menuRepository, RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public Restaurant getRestaurantById(Long id){
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Restaurant not found"));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<RestaurantResponse> getAllRestaurants() {
       return restaurantRepository.findAll().stream()
               .map(restaurantMapper::toRestaurantResponse)
               .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public RestaurantResponse getRestaurantByName(String restaurantName) {
        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(()->new ResourceNotFound("Restaurant not found"));
        return restaurantMapper.toRestaurantResponse(restaurant);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    public RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest) {
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
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and ownershipService.checkRestaurantOwnership(authentication.principal.id)")
    public void updateRestaurant(Long id, RestaurantRequest restaurantRequest) {
        Restaurant restaurantToUpdate = getRestaurantById(id);
        Restaurant updatedRestaurant = setRestaurantToUpdateProperties(
                restaurantToUpdate,
                restaurantRequest
        );
        restaurantRepository.save(updatedRestaurant);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and ownershipService.checkRestaurantOwnership(authentication.principal.id)")
    public void deleteRestaurant(Long id) {
        Restaurant  restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and ownershipService.checkRestaurantOwnership(authentication.principal.id)")
    public void removeMenuFromRestaurant(Long restaurantId, Long menuId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        Menu menu = getMenuById(menuId);
        restaurant.removeMenu(menu);
        restaurantRepository.save(restaurant);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and ownershipService.checkRestaurantOwnership(authentication.principal.id)")
    public RestaurantResponse getRestaurantByOwnerId(Long ownerId) {
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
