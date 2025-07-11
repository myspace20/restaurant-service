package com.bytebites.restaurant_service.services;

import com.bytebites.restaurant_service.dto.RestaurantRequest;
import com.bytebites.restaurant_service.dto.RestaurantResponse;
import com.bytebites.restaurant_service.exceptions.ResourceNotFound;
import com.bytebites.restaurant_service.mappers.RestaurantMapper;
import com.bytebites.restaurant_service.models.Menu;
import com.bytebites.restaurant_service.models.Restaurant;
import com.bytebites.restaurant_service.repositories.MenuRepository;
import com.bytebites.restaurant_service.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RestaurantServiceImplTest {

    private RestaurantRepository restaurantRepository;
    private MenuRepository menuRepository;
    private RestaurantMapper restaurantMapper;
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        menuRepository = mock(MenuRepository.class);
        restaurantMapper = mock(RestaurantMapper.class);
        restaurantService = new RestaurantServiceImpl(menuRepository, restaurantRepository, restaurantMapper);
    }

    @Test
    void testGetRestaurantById_Success() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getRestaurantById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetRestaurantById_NotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> restaurantService.getRestaurantById(1L));
    }

    @Test
    void testGetAllRestaurants() {
        Restaurant restaurant = new Restaurant();
        RestaurantResponse response = new RestaurantResponse(
                10L,
                "ByteBites",
                "123 Tech Ave",
                "+1234567890",
                "bytebites@email.com",
                1L,
                List.of()
        );

        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));
        when(restaurantMapper.toRestaurantResponse(any())).thenReturn(response);

        List<RestaurantResponse> result = restaurantService.getAllRestaurants();
        assertEquals(1, result.size());
    }

    @Test
    void testGetRestaurantByName_Success() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test");
        RestaurantResponse response = new RestaurantResponse(
                1L,
                "Test",
                "123 Tech Ave",
                "+1234567890",
                "bytebites@email.com",
                1L,
                List.of()
        );

        when(restaurantRepository.findByName("Test")).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toRestaurantResponse(restaurant)).thenReturn(response);

        RestaurantResponse result = restaurantService.getRestaurantByName("Test");
        assertEquals(restaurant.getName(), result.name());
    }

    @Test
    void testCreateRestaurant_Success() {

        RestaurantRequest request = new RestaurantRequest(
                "Tet", "Address", "123456", "test@email.com", 1L
        );

        Restaurant savedRestaurant = new Restaurant(
                "Test", "Address", "123456", "test@email.com", 1L
        );
        savedRestaurant.setId(10L);

        RestaurantResponse expectedResponse = new RestaurantResponse(
                10L,
                "Test",
                "Address",
                "123456",
                "test@email.com",
                1L,
                List.of()
        );

        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);
        when(restaurantRepository.save(restaurantCaptor.capture())).thenReturn(savedRestaurant);
        when(restaurantMapper.toRestaurantResponse(savedRestaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.createRestaurant(request);

        assertEquals(expectedResponse, result);

        Restaurant captured = restaurantCaptor.getValue();
        assertEquals(request.name(), captured.getName());
        assertEquals(request.address(), captured.getAddress());
        assertEquals(request.phone(), captured.getPhone());
        assertEquals(request.email(), captured.getEmail());
    }

    @Test
    void testUpdateRestaurant_Success() {
        Restaurant restaurant = new Restaurant("Old", "Old Address", "000", "old@email.com", 1L);
        restaurant.setId(1L);

        RestaurantRequest request = new RestaurantRequest("New", "New Address", "111", "new@email.com", 1L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any())).thenReturn(restaurant);

        restaurantService.updateRestaurant(1L, request);

        assertEquals("New", restaurant.getName());
        assertEquals("New Address", restaurant.getAddress());
    }

    @Test
    void testDeleteRestaurant_Success() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantRepository).delete(restaurant);

        restaurantService.deleteRestaurant(1L);
        verify(restaurantRepository).delete(restaurant);
    }

    @Test
    void testRemoveMenuFromRestaurant() {
        Restaurant restaurant = mock(Restaurant.class);
        Menu menu = new Menu();
        menu.setId(5L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuRepository.findById(5L)).thenReturn(Optional.of(menu));

        restaurantService.removeMenuFromRestaurant(1L, 5L);

        verify(restaurant).removeMenu(menu);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void testGetRestaurantByOwnerId_Success() {
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId(10L);
        RestaurantResponse response = new RestaurantResponse(
                10L,
                "ByteBites",
                "123 Tech Ave",
                "+1234567890",
                "bytebites@email.com",
                1L,
                List.of()
        );

        when(restaurantRepository.findByOwnerId(10L)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toRestaurantResponse(restaurant)).thenReturn(response);

        RestaurantResponse result = restaurantService.getRestaurantByOwnerId(10L);
        assertEquals(response, result);
    }

    @Test
    void testUpdateRestaurant_NotFound() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        RestaurantRequest request = new RestaurantRequest("Name", "Addr", "Phone", "email", 1L);

        assertThrows(ResourceNotFound.class, () -> restaurantService.updateRestaurant(999L, request));
    }

    @Test
    void testRemoveMenu_RestaurantNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> restaurantService.removeMenuFromRestaurant(1L, 5L));
    }

    @Test
    void testRemoveMenu_MenuNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(new Restaurant()));
        when(menuRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> restaurantService.removeMenuFromRestaurant(1L, 5L));
    }

}

