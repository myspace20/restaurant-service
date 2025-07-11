package com.bytebites.restaurant_service.repositories;

import com.bytebites.restaurant_service.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByName(String name);
    Optional<Restaurant> findByOwnerId(Long id);
    boolean existsByIdAndOwnerId(Long restaurantId,Long ownerId);
}
