package com.bytebites.restaurant_service.controllers;


import com.bytebites.restaurant_service.dto.MenuRequest;
import com.bytebites.restaurant_service.dto.MenuResponse;
import com.bytebites.restaurant_service.services.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(@RequestBody MenuRequest menuRequest) {
        return ResponseEntity.ok(menuService.createMenu(menuRequest));
    }

    @GetMapping
    public ResponseEntity<MenuResponse> getMenuByName(@Valid @RequestParam String name) {
        return ResponseEntity.ok( menuService.findMenuByName(name));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateMenu(@Valid @PathVariable Long id,@RequestBody MenuRequest menuRequest) {
        menuService.updateMenu(id, menuRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteMenu(@Valid @PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}
