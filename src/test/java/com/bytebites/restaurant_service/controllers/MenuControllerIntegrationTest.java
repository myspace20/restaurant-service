package com.bytebites.restaurant_service.controllers;

import com.bytebites.restaurant_service.dto.MenuRequest;
import com.bytebites.restaurant_service.models.Menu;
import com.bytebites.restaurant_service.models.Restaurant;
import com.bytebites.restaurant_service.repositories.MenuRepository;
import com.bytebites.restaurant_service.repositories.RestaurantRepository;
import com.bytebites.restaurant_service.utilities.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class MenuControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final JWTUtil jwtUtil;

    public MenuControllerIntegrationTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            RestaurantRepository restaurantRepository,
            MenuRepository menuRepository,
            JWTUtil jwtUtil
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.jwtUtil = jwtUtil;
    }


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setupContainer() {
        postgres.start();
        System.setProperty("SPRING_DATASOURCE_URL", postgres.getJdbcUrl());
        System.setProperty("SPRING_DATASOURCE_USERNAME", postgres.getUsername());
        System.setProperty("SPRING_DATASOURCE_PASSWORD", postgres.getPassword());
    }

    @AfterEach
    void cleanup() {
        menuRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    private String generateToken(Long ownerId) {
        return "Bearer " + jwtUtil.generateAccessToken(ownerId, List.of("ROLE_RESTAURANT_OWNER"));
    }

    @Test
    @Order(1)
    void testCreateMenu() throws Exception {
        Restaurant restaurant = restaurantRepository.save(new Restaurant("Testaurant", "123 St", "123", "a@b.com", 88L));
        String token = generateToken(88L);

        MenuRequest request = new MenuRequest("Pizza", "Cheesy delight", 9.99, restaurant.getId());

        mockMvc.perform(post("/api/v1/menu")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    @Order(2)
    void testGetMenuByName() throws Exception {
        Restaurant restaurant = restaurantRepository.save(new Restaurant("Resto", "City", "123", "c@d.com", 10L));
        Menu menu = new Menu("Burger", "Juicy", 5.50);
        menu.setRestaurant(restaurant);
        menuRepository.save(menu);

        String token = generateToken(10L);

        mockMvc.perform(get("/api/v1/menu")
                        .header("Authorization", token)
                        .param("name", "Burger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Burger"));
    }

    @Test
    @Order(3)
    void testGetMenuByName_NotFound() throws Exception {
        Restaurant restaurant = restaurantRepository.save(new Restaurant("Resto", "City", "123", "c@d.com", 10L));
        Menu menu = new Menu("BurgerNotFound", "Juicy", 5.50);
        menu.setRestaurant(restaurant);
        menuRepository.save(menu);

        String token = generateToken(10L);

        mockMvc.perform(get("/api/v1/menu")
                        .header("Authorization", token)
                        .param("name", "Burger"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void testUpdateMenu() throws Exception {
        Restaurant restaurant = restaurantRepository.save(new Restaurant("OwnerResto", "Add", "999", "o@b.com", 5L));
        Menu menu = new Menu("Rice", "Fried", 6.00);
        menu.setRestaurant(restaurant);
        menu = menuRepository.save(menu);

        String token = generateToken(5L);

        MenuRequest update = new MenuRequest("Fried Rice", "Spicy fried rice", 7.50, restaurant.getId());

        mockMvc.perform(put("/api/v1/menu/" + menu.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(5)
    void testDeleteMenu() throws Exception {
        Restaurant restaurant = restaurantRepository.save(new Restaurant("DeleteResto", "Loc", "333", "del@byte.com", 77L));
        Menu menu = new Menu("Pasta", "Creamy", 8.80);
        menu.setRestaurant(restaurant);
        menu = menuRepository.save(menu);

        String token = generateToken(77L);

        mockMvc.perform(delete("/api/v1/menu/" + menu.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        Assertions.assertTrue(menuRepository.findById(menu.getId()).isEmpty());
    }

    @Test
    @Order(6)
    void testUpdateMenu_AccessDenied() throws Exception {
        Restaurant restaurant = restaurantRepository.save(new Restaurant("OwnerResto", "Add", "999", "o@b.com", 10L));
        Menu menu = new Menu("Rice", "Fried", 6.00);
        menu.setRestaurant(restaurant);
        menu = menuRepository.save(menu);

        String token = generateToken(5L);

        MenuRequest update = new MenuRequest("Fried Rice", "Spicy fried rice", 7.50, restaurant.getId());

        mockMvc.perform(put("/api/v1/menu/" + menu.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    void testDeleteMenu_AccessDenied() throws Exception {
        Restaurant restaurant = restaurantRepository.save(new Restaurant("DeleteResto", "Loc", "333", "del@byte.com", 7L));
        Menu menu = new Menu("Pasta", "Creamy", 8.80);
        menu.setRestaurant(restaurant);
        menu = menuRepository.save(menu);

        String token = generateToken(77L);

        mockMvc.perform(delete("/api/v1/menu/" + menu.getId())
                        .header("Authorization", token))
                .andExpect(status().isForbidden());

    }
}
