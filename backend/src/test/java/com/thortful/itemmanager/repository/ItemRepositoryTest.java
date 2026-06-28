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

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        itemRepository.saveAll(List.of(
                new Item("Granite Stone"),   // contains "stone"
                new Item("Limestone Block"),  // contains "stone"
                new Item("Quartz Arch"),      // no match for "stone"
                new Item("Obsidian Shard"),   // no match for "stone"
                new Item("Marble Column"),    // no match for "stone"
                new Item("Cobblestone Path"), // contains "stone"
                new Item("Basalt Rock"),      // no match for "stone"
                new Item("Amber Crystal"),    // no match for "stone"
                new Item("STONE TOWER"),      // contains "stone" (uppercase)
                new Item("river stone")       // contains "stone"
        ));
    }

    // -------------------------------------------------------------------------
    // Exact fragment match
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns items whose name contains the given fragment")
    void shouldReturnItemsContainingFragment() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("Marble", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Marble Column");
    }

    // -------------------------------------------------------------------------
    // Case-insensitive matching
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Matches regardless of the fragment being uppercase")
    void shouldMatchWhenFragmentIsUpperCase() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("AMBER", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Amber Crystal");
    }

    @Test
    @DisplayName("Matches regardless of the fragment being lowercase")
    void shouldMatchWhenFragmentIsLowerCase() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("basalt", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Basalt Rock");
    }

    @Test
    @DisplayName("Matches items stored in uppercase when fragment is lowercase")
    void shouldMatchUpperCaseStoredNameWithLowerCaseFragment() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone tower", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("STONE TOWER");
    }

    // -------------------------------------------------------------------------
    // Shared fragment across multiple items
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns all items whose name contains a shared fragment")
    void shouldReturnMultipleItemsForSharedFragment() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone", pageable);

        // Matches: "Granite Stone", "Sandstone Arch", "Cobblestone Path", "STONE TOWER", "river stone"
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    // -------------------------------------------------------------------------
    // No match
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns an empty page when no item matches the fragment")
    void shouldReturnEmptyPageWhenNoMatchFound() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("Diamond", pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // -------------------------------------------------------------------------
    // Empty fragment
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Returns all items when the fragment is an empty string")
    void shouldReturnAllItemsForEmptyFragment() {
        Pageable pageable = PageRequest.of(0, 20);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("", pageable);

        assertThat(result.getTotalElements()).isEqualTo(10);
    }

    // -------------------------------------------------------------------------
    // Pagination behaviour
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Respects page size and returns correct total pages")
    void shouldRespectPageSizeAndTotalPages() {
        Pageable pageable = PageRequest.of(0, 3);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone", pageable);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Returns correct items on the second page")
    void shouldReturnCorrectItemsOnSecondPage() {
        Pageable firstPage  = PageRequest.of(0, 3, Sort.by("name").ascending());
        Pageable secondPage = PageRequest.of(1, 3, Sort.by("name").ascending());

        Page<Item> first  = itemRepository.findByNameContainingIgnoreCase("stone", firstPage);
        Page<Item> second = itemRepository.findByNameContainingIgnoreCase("stone", secondPage);

        assertThat(first.getContent()).hasSize(3);
        assertThat(second.getContent()).hasSize(2);
        assertThat(first.getContent()).doesNotContainAnyElementsOf(second.getContent());
    }

    @Test
    @DisplayName("Returns an empty page when the requested page index exceeds total pages")
    void shouldReturnEmptyPageWhenPageIndexExceedsTotalPages() {
        Pageable pageable = PageRequest.of(99, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("stone", pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    // -------------------------------------------------------------------------
    // Single-character fragment
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Matches items using a single-character fragment")
    void shouldMatchWithSingleCharacterFragment() {
        Pageable pageable = PageRequest.of(0, 20);

        // "x" does not appear in any of the 10 seeded names
        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("x", pageable);

        assertThat(result.getContent()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Fragment matches full name
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Matches when the fragment is identical to the full item name")
    void shouldMatchWhenFragmentEqualsFullName() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByNameContainingIgnoreCase("Amber Crystal", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Amber Crystal");
    }
}
