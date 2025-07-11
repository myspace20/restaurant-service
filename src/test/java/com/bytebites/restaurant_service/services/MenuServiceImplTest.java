package com.bytebites.restaurant_service.services;

import com.bytebites.restaurant_service.dto.MenuRequest;
import com.bytebites.restaurant_service.dto.MenuResponse;
import com.bytebites.restaurant_service.exceptions.ResourceNotFound;
import com.bytebites.restaurant_service.mappers.MenuMapper;
import com.bytebites.restaurant_service.models.Menu;
import com.bytebites.restaurant_service.models.Restaurant;
import com.bytebites.restaurant_service.repositories.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuServiceImplTest {

    private MenuRepository menuRepository;
    private MenuMapper menuMapper;
    private RestaurantService restaurantService;
    private MenuService menuService;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        this.menuRepository = mock(MenuRepository.class);
        this.menuMapper = mock(MenuMapper.class);
        this.restaurantService = mock(RestaurantService.class);
        this.menuService = new MenuServiceImpl(menuRepository, menuMapper, restaurantService);
        restaurant = new Restaurant("ByteBites", "123 Street", "123456", "contact@byte.com", 1L);
        restaurant.setId(1L);
    }

    @Test
    void findMenuByName_shouldReturnMenuResponse_whenMenuExists() {
        String name = "Burger";
        Menu menu = new Menu("Burger", "Tasty", 5.0);
        menu.setId(1L);
        menu.setRestaurant(restaurant);
        MenuResponse response = new MenuResponse(1L, "Burger", "Tasty", 5.0);

        when(menuRepository.findByName(name)).thenReturn(Optional.of(menu));
        when(menuMapper.toMenuResponse(menu)).thenReturn(response);

        MenuResponse result = menuService.findMenuByName(name);

        assertEquals("Burger", result.name());
        verify(menuRepository).findByName(name);
        verify(menuMapper).toMenuResponse(menu);
    }

    @Test
    void findMenuByName_shouldThrow_whenMenuNotFound() {
        when(menuRepository.findByName("NotExist")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> menuService.findMenuByName("NotExist"));
        verify(menuRepository).findByName("NotExist");
    }

    @Test
    void createMenu_shouldSaveAndReturnMenuResponse() {
        MenuRequest request = new MenuRequest("Pizza", "Cheesy", 10.0, 1L);
        Menu menu = new Menu("Pizza", "Cheesy", 10.0);
        menu.setRestaurant(restaurant);
        Menu savedMenu = new Menu("Pizza", "Cheesy", 10.0);
        savedMenu.setId(1L);
        savedMenu.setRestaurant(restaurant);
        MenuResponse response = new MenuResponse(1L, "Pizza", "Cheesy", 10.0);

        when(restaurantService.getRestaurantById(1L)).thenReturn(restaurant);
        when(menuRepository.save(any(Menu.class))).thenReturn(savedMenu);
        when(menuMapper.toMenuResponse(savedMenu)).thenReturn(response);

        MenuResponse result = menuService.createMenu(request);

        assertEquals("Pizza", result.name());
        verify(restaurantService).getRestaurantById(1L);
        verify(menuRepository).save(any(Menu.class));
        verify(menuMapper).toMenuResponse(savedMenu);
    }

    @Test
    void updateMenu_shouldUpdateAndSaveMenu() {
        Long menuId = 1L;
        Menu menu = new Menu("OldName", "OldDesc", 5.0);
        menu.setId(menuId);
        menu.setRestaurant(restaurant);

        MenuRequest request = new MenuRequest("NewName", "NewDesc", 8.5, 1L);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        menuService.updateMenu(menuId, request);

        assertEquals("NewName", menu.getName());
        assertEquals("NewDesc", menu.getDescription());
        assertEquals(8.5, menu.getPrice());
        verify(menuRepository).save(menu);
    }

    @Test
    void updateMenu_shouldThrow_whenMenuNotFound() {
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> menuService.updateMenu(99L, new MenuRequest("N", "D", 1.0, 1L)));
        verify(menuRepository).findById(99L);
    }

    @Test
    void deleteMenu_shouldDeleteMenuIfExists() {
        Long menuId = 1L;
        Menu menu = new Menu("ToDelete", "Desc", 3.0);
        menu.setId(menuId);
        menu.setRestaurant(restaurant);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        menuService.deleteMenu(menuId);

        verify(menuRepository).delete(menu);
    }

    @Test
    void deleteMenu_shouldThrow_whenMenuNotFound() {
        when(menuRepository.findById(55L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> menuService.deleteMenu(55L));
        verify(menuRepository).findById(55L);
    }
}
