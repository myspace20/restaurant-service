package com.bytebites.restaurant_service.mappers;

import com.bytebites.restaurant_service.dto.RestaurantResponse;
import com.bytebites.restaurant_service.models.Restaurant;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring",uses = MenuMapper.class)
public interface RestaurantMapper {
    RestaurantResponse toRestaurantResponse(Restaurant restaurant);
}
