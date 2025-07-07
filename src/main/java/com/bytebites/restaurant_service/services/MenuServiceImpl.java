package com.bytebites.restaurant_service.services;

import com.bytebites.restaurant_service.dto.MenuRequest;
import com.bytebites.restaurant_service.dto.MenuResponse;
import com.bytebites.restaurant_service.mappers.MenuMapper;
import com.bytebites.restaurant_service.models.Menu;
import com.bytebites.restaurant_service.models.Restaurant;
import com.bytebites.restaurant_service.repositories.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final RestaurantService restaurantService;


    public MenuServiceImpl(MenuRepository menuRepository, MenuMapper menuMapper, RestaurantService restaurantService) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
        this.restaurantService = restaurantService;
    }

    @Override
    public MenuResponse findMenuByName(String name) {
        Menu menu = menuRepository.findByName(name)
                .orElseThrow(()->new RuntimeException("Menu not found"));
        return menuMapper.toMenuResponse(menu);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and ownershipService.checkRestaurantOwnership(authentication.principal.id)")
    public MenuResponse createMenu(MenuRequest menuRequest) {
       Restaurant restaurant = restaurantService
               .getRestaurantById(menuRequest.restaurant_id());
        Menu menu = new Menu(
                menuRequest.name(),
                menuRequest.description(),
                menuRequest.price()
        );
        menu.setRestaurant(restaurant);
        Menu newMenu = menuRepository.save(menu);
        return menuMapper.toMenuResponse(newMenu);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and ownershipService.checkRestaurantOwnership(authentication.principal.id)")
    public void updateMenu(Long id, MenuRequest menuRequest) {
       Menu menuToUpdate = findMenuById(id);
       Menu updatedMenu = setMenuToUpdateAttributes(
               menuToUpdate,
               menuRequest
       );
       menuRepository.save(updatedMenu);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER') and ownershipService.checkRestaurantOwnership(authentication.principal.id)")
    public void deleteMenu(Long id) {
        Menu menuToDelete = findMenuById(id);
        menuRepository.delete(menuToDelete);
    }

    private Menu setMenuToUpdateAttributes(Menu menu, MenuRequest menuRequest) {
        menu.setName(menuRequest.name());
        menu.setDescription(menuRequest.description());
        menu.setPrice(menuRequest.price());
        return menu;
    }

    private Menu findMenuById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Menu not found"));
    }
}
