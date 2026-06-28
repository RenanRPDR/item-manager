package com.thortful.itemmanager.controller;

import com.thortful.itemmanager.model.Item;
import com.thortful.itemmanager.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Items", description = "Item Management API")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "Get all items", description = "Returns a paginated, case-insensitive search over item names. An empty or absent search parameter returns all items.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved items")
    @GetMapping
    public ResponseEntity<Page<Item>> getItems(
            @Parameter(description = "Search fragment to filter by name") @RequestParam(defaultValue = "") String search,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Item> result = itemService.searchByName(search, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create a new item", description = "Creates a new item and returns it with HTTP 201 Created.")
    @ApiResponse(responseCode = "201", description = "Item successfully created")
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item saved = itemService.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Delete an item", description = "Removes the item with the given ID and returns HTTP 204 No Content.")
    @ApiResponse(responseCode = "204", description = "Item successfully deleted")
    @ApiResponse(responseCode = "404", description = "Item not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "ID of the item to be deleted") @PathVariable Long id) {
        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
