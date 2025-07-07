package com.bytebites.restaurant_service.services;


import com.bytebites.restaurant_service.dto.MenuRequest;
import com.bytebites.restaurant_service.dto.MenuResponse;


public interface MenuService {
    MenuResponse createMenu(MenuRequest menuRequest);
    MenuResponse findMenuByName(String menuName);
    void updateMenu(Long id,MenuRequest menuRequest);
    void deleteMenu(Long id);
}
