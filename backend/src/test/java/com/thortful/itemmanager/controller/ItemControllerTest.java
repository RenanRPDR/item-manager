package com.thortful.itemmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.thortful.itemmanager.model.Item;
import com.thortful.itemmanager.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@DisplayName("ItemController — Unit Tests")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    private ObjectMapper objectMapper;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        item1 = new Item("Apple");
        item1.setId(1L);
        
        item2 = new Item("Banana");
        item2.setId(2L);
    }

    @Test
    @DisplayName("GET /api/items should return a paginated list of items")
    void getItems_shouldReturnPaginatedItems() throws Exception {
        // Given
        Page<Item> page = new PageImpl<>(List.of(item1, item2));
        Mockito.when(itemService.searchByName(eq("app"), any())).thenReturn(page);

        // When
        var response = mockMvc.perform(get("/api/items")
                .param("search", "app")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Apple")))
                .andExpect(jsonPath("$.content[1].name", is("Banana")));
    }

    @Test
    @DisplayName("POST /api/items should create an item and return 201 Created")
    void createItem_shouldCreateAndReturnItem() throws Exception {
        // Given
        Item inputItem = new Item("Orange");
        Item savedItem = new Item("Orange");
        savedItem.setId(10L);
        Mockito.when(itemService.save(any(Item.class))).thenReturn(savedItem);

        // When
        var response = mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputItem)));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Orange")));
    }

    @Test
    @DisplayName("DELETE /api/items/{id} should delete item and return 204 No Content")
    void deleteItem_shouldReturn204() throws Exception {
        // Given
        Long idToDelete = 1L;
        Mockito.doNothing().when(itemService).deleteById(idToDelete);

        // When
        var response = mockMvc.perform(delete("/api/items/{id}", idToDelete));

        // Then
        response.andExpect(status().isNoContent());
        Mockito.verify(itemService, Mockito.times(1)).deleteById(idToDelete);
    }

    @Test
    @DisplayName("DELETE /api/items/{id} should return 404 when item does not exist")
    void deleteItem_shouldReturn404WhenItemNotFound() throws Exception {
        // Given
        Long idToDelete = 99L;
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(itemService).deleteById(idToDelete);

        // When
        var response = mockMvc.perform(delete("/api/items/{id}", idToDelete));

        // Then
        response.andExpect(status().isNotFound());
    }
}
