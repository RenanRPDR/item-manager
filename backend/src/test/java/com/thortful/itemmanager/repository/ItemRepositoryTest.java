package com.thortful.itemmanager.repository;

import com.thortful.itemmanager.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ItemRepository — Paginated Search Integration Tests")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        defaultPageable = PageRequest.of(0, 10);
        itemRepository.deleteAll();
        itemRepository.saveAll(List.of(
                new Item("Granite Stone"), // contains "stone"
                new Item("Limestone Block"), // contains "stone"
                new Item("Quartz Arch"), // no match for "stone"
                new Item("Obsidian Shard"), // no match for "stone"
                new Item("Marble Column"), // no match for "stone"
                new Item("Cobblestone Path"), // contains "stone"
                new Item("Basalt Rock"), // no match for "stone"
                new Item("Amber Crystal"), // no match for "stone"
                new Item("STONE TOWER"), // contains "stone" (uppercase)
                new Item("river stone") // contains "stone"
        ));
    }

    // -------------------------------------------------------------------------
    // Exact fragment match
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns items whose name contains the given fragment")
    void shouldReturnItemsContainingFragment() {
        // Given (setup is in BeforeEach)

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("Marble", defaultPageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Marble Column");
    }

    // -------------------------------------------------------------------------
    // Case-insensitive matching
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Matches regardless of the fragment being uppercase")
    void shouldMatchWhenFragmentIsUpperCase() {
        // Given

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("AMBER", defaultPageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Amber Crystal");
    }

    @Test
    @DisplayName("Matches regardless of the fragment being lowercase")
    void shouldMatchWhenFragmentIsLowerCase() {
        // Given

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("basalt", defaultPageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Basalt Rock");
    }

    @Test
    @DisplayName("Matches items stored in uppercase when fragment is lowercase")
    void shouldMatchUpperCaseStoredNameWithLowerCaseFragment() {
        // Given

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone tower", defaultPageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("STONE TOWER");
    }

    // -------------------------------------------------------------------------
    // Shared fragment across multiple items
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns all items whose name contains a shared fragment")
    void shouldReturnMultipleItemsForSharedFragment() {
        // Given

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone", defaultPageable);

        // Then
        // Matches: "Granite Stone", "Limestone Block", "Cobblestone Path", "STONE
        // TOWER", "river stone"
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    // -------------------------------------------------------------------------
    // No match
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns an empty page when no item matches the fragment")
    void shouldReturnEmptyPageWhenNoMatchFound() {
        // Given

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("Diamond", defaultPageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // -------------------------------------------------------------------------
    // Empty fragment
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns all items when the fragment is an empty string")
    void shouldReturnAllItemsForEmptyFragment() {
        // Given
        Pageable largePageable = PageRequest.of(0, 20);

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("", largePageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(10);
    }

    // -------------------------------------------------------------------------
    // Pagination behaviour
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Respects page size and returns correct total pages")
    void shouldRespectPageSizeAndTotalPages() {
        // Given
        Pageable smallPageable = PageRequest.of(0, 3);

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone", smallPageable);

        // Then
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Returns correct items on the second page")
    void shouldReturnCorrectItemsOnSecondPage() {
        // Given
        Pageable firstPage = PageRequest.of(0, 3, Sort.by("name").ascending());
        Pageable secondPage = PageRequest.of(1, 3, Sort.by("name").ascending());

        // When
        Page<Item> first = itemRepository.findByNameContainingIgnoreCase("stone", firstPage);
        Page<Item> second = itemRepository.findByNameContainingIgnoreCase("stone", secondPage);

        // Then
        assertThat(first.getContent()).hasSize(3);
        assertThat(second.getContent()).hasSize(2);
        assertThat(first.getContent()).doesNotContainAnyElementsOf(second.getContent());
    }

    @Test
    @DisplayName("Returns an empty page when the requested page index exceeds total pages")
    void shouldReturnEmptyPageWhenPageIndexExceedsTotalPages() {
        // Given
        Pageable excessivePageable = PageRequest.of(99, 10);

        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone", excessivePageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    // -------------------------------------------------------------------------
    // Single-character fragment
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Matches items using a single-character fragment")
    void shouldMatchWithSingleCharacterFragment() {
        // Given
        Pageable largePageable = PageRequest.of(0, 20);

        // When
        // "x" does not appear in any of the 10 seeded names
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("x", largePageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Fragment matches full name
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Matches when the fragment is identical to the full item name")
    void shouldMatchWhenFragmentEqualsFullName() {
        // When
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("Amber Crystal", defaultPageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Amber Crystal");
    }
}
