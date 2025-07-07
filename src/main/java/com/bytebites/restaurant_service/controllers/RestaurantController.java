package com.bytebites.restaurant_service.controllers;


import com.bytebites.restaurant_service.dto.RestaurantRequest;
import com.bytebites.restaurant_service.dto.RestaurantResponse;
import com.bytebites.restaurant_service.services.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest) {
        return ResponseEntity.ok(restaurantService.createRestaurant(restaurantRequest));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{name}")
    public ResponseEntity<RestaurantResponse> getRestaurantByName(@Valid @PathVariable String name) {
        return ResponseEntity.ok(restaurantService.getRestaurantByName(name));
    }

    @PutMapping("/{id}")
    public void updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantRequest restaurantRequest) {
        restaurantService.updateRestaurant(id, restaurantRequest);
    }


    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<RestaurantResponse> getRestaurantByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(restaurantService.getRestaurantByOwnerId(ownerId));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/menu/{menuId}")
    public void removeMenuToRestaurant(@PathVariable Long id, @PathVariable Long menuId) {
        restaurantService.removeMenuFromRestaurant(id, menuId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteRestaurant(@Valid @PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
    }
}
