package com.bytebites.restaurant_service.controllers;

import com.bytebites.restaurant_service.dto.RestaurantRequest;
import com.bytebites.restaurant_service.models.Restaurant;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class RestaurantControllerIntegrationTest {

    private final JWTUtil jwtUtil;
    private final MockMvc mockMvc;
    private final RestaurantRepository restaurantRepository;
    private final ObjectMapper objectMapper;

    public RestaurantControllerIntegrationTest(JWTUtil jwtUtil,
                                               MockMvc mockMvc,
                                               RestaurantRepository restaurantRepository,
                                               ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.mockMvc = mockMvc;
        this.restaurantRepository = restaurantRepository;
        this.objectMapper = objectMapper;
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("restaurantdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setup() {
        postgres.start();
        System.setProperty("SPRING_DATASOURCE_URL", postgres.getJdbcUrl());
        System.setProperty("SPRING_DATASOURCE_USERNAME", postgres.getUsername());
        System.setProperty("SPRING_DATASOURCE_PASSWORD", postgres.getPassword());
    }

    @AfterEach
    void cleanup() {
        restaurantRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testCreateRestaurant() throws Exception {
        RestaurantRequest request = new RestaurantRequest(
                "Testaurant", "123 Java St", "1234567890", "test@byte.com", 42L
        );
        String jwt = generateJwtToken(2L, List.of("ROLE_RESTAURANT_OWNER"));
        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Testaurant"));
    }

    @Test
    @Order(2)
    void testGetAllRestaurants() throws Exception {
        restaurantRepository.save(new Restaurant("R1", "Addr1", "111", "a@b.com", 1L));
        restaurantRepository.save(new Restaurant("R2", "Addr2", "222", "b@b.com", 2L));

        String jwt = generateJwtToken(2L, List.of("ROLE_RESTAURANT_OWNER"));

        mockMvc.perform(get("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Order(3)
    void testGetRestaurantByName() throws Exception {
        restaurantRepository.save(new Restaurant("Kofi's", "Lagos", "777", "kofi@byte.com", 88L));

        String jwt = generateJwtToken(2L, List.of("ROLE_RESTAURANT_OWNER"));

        mockMvc.perform(get("/api/v1/restaurants/Kofi's")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kofi's"));
    }

    @Test
    @Order(4)
    void testUpdateRestaurantSuccess() throws Exception {
        Restaurant saved = restaurantRepository.save(
                new Restaurant("Old", "Old", "000", "old@email.com", 2L)
        );
        String jwt = generateJwtToken(2L, List.of("ROLE_RESTAURANT_OWNER"));

        RestaurantRequest update = new RestaurantRequest("New", "New Addr", "999", "new@byte.com", 2L);

        mockMvc.perform(put("/api/v1/restaurants/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
        Restaurant updated = restaurantRepository.findById(saved.getId()).get();
        Assertions.assertEquals("New", updated.getName());
    }

    @Test
    @Order(4)
    void testUpdateRestaurantAccessDenied() throws Exception {
        Restaurant saved = restaurantRepository.save(
                new Restaurant("Old", "Old", "000", "old@email.com", 20L)
        );
        String jwt = generateJwtToken(2L, List.of("ROLE_RESTAURANT_OWNER"));

        RestaurantRequest update = new RestaurantRequest("New", "New Addr", "999", "new@byte.com", 2L);

        mockMvc.perform(put("/api/v1/restaurants/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    void testGetRestaurantByOwnerId() throws Exception {
        restaurantRepository.save(new Restaurant("Test", "Test", "000", "own@byte.com", 15L));

        String jwt = generateJwtToken(15L, List.of("ROLE_RESTAURANT_OWNER"));

        mockMvc.perform(get("/api/v1/restaurants/owners/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(15));
    }

    @Test
    @Order(6)
    void testDeleteRestaurantSuccess() throws Exception {
        Restaurant saved = restaurantRepository.save(new Restaurant("ToDelete", "Del", "123", "del@byte.com", 2L));

        String jwt = generateJwtToken(2L, List.of("ROLE_RESTAURANT_OWNER"));

        mockMvc.perform(delete("/api/v1/restaurants/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                )
                .andExpect(status().isNoContent());

        Assertions.assertTrue(restaurantRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    @Order(7)
    void testDeleteRestaurantAccessDenied() throws Exception {
        Restaurant saved = restaurantRepository.save(new Restaurant("ToDelete", "Del", "123", "del@byte.com", 3L));

        String jwt = generateJwtToken(2L, List.of("ROLE_RESTAURANT_OWNER"));

        mockMvc.perform(delete("/api/v1/restaurants/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",jwt)
                )
                .andExpect(status().isForbidden());
    }



    private String generateJwtToken(Long userId, List<String> roles) {
        return "Bearer " + jwtUtil.generateAccessToken(userId, roles);
    }
}
