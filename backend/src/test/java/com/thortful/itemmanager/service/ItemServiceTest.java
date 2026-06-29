package com.thortful.itemmanager.service;

import java.util.UUID;

import org.springframework.web.server.ResponseStatusException;
import com.thortful.itemmanager.model.Item;
import com.thortful.itemmanager.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService — Unit Tests")
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item1;
    private Item item2;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        item1 = new Item("Granite");
        item1.setId(UUID.randomUUID());

        item2 = new Item("Marble");
        item2.setId(UUID.randomUUID());
        
        defaultPageable = PageRequest.of(0, 10);
    }

    // -------------------------------------------------------------------------
    // findAll cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll returns the full list from the repository")
    void findAll_shouldReturnAllItems() {
        // Given
        List<Item> items = List.of(item1, item2);
        when(itemRepository.findAll()).thenReturn(items);

        // When
        List<Item> result = itemService.findAll();

        // Then
        assertThat(result).hasSize(2).containsExactlyElementsOf(items);
        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("findAll returns an empty list when the repository is empty")
    void findAll_shouldReturnEmptyListWhenNoItems() {
        // Given
        when(itemRepository.findAll()).thenReturn(List.of());

        // When
        List<Item> result = itemService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(itemRepository).findAll();
    }

    // -------------------------------------------------------------------------
    // searchByName cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("searchByName forwards the fragment and pageable to the repository")
    void searchByName_shouldForwardFragmentAndPageableToRepository() {
        // Given
        String fragment = "stone";
        Page<Item> expectedPage = new PageImpl<>(List.of(item1));

        when(itemRepository.findByNameContainingIgnoreCase(fragment, defaultPageable))
                .thenReturn(expectedPage);

        // When
        Page<Item> result = itemService.searchByName(fragment, defaultPageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(itemRepository).findByNameContainingIgnoreCase(fragment, defaultPageable);
    }

    @Test
    @DisplayName("searchByName returns an empty page when no items match the fragment")
    void searchByName_shouldReturnEmptyPageWhenNoMatch() {
        // Given
        String fragment = "zzz";
        Page<Item> emptyPage = Page.empty(defaultPageable);

        when(itemRepository.findByNameContainingIgnoreCase(fragment, defaultPageable))
                .thenReturn(emptyPage);

        // When
        Page<Item> result = itemService.searchByName(fragment, defaultPageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("searchByName uses the correct page number and page size")
    void searchByName_shouldUseCorrectPaginationParameters() {
        // Given
        Pageable pageable = PageRequest.of(2, 5);
        Page<Item> page = new PageImpl<>(List.of(), pageable, 0);

        when(itemRepository.findByNameContainingIgnoreCase("rock", pageable))
                .thenReturn(page);

        // When
        itemService.searchByName("rock", pageable);

        // Then
        verify(itemRepository).findByNameContainingIgnoreCase(
                argThat(f -> f.equals("rock")),
                argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 5));
    }

    // -------------------------------------------------------------------------
    // save cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save persists the item and returns the saved entity")
    void save_shouldPersistItemAndReturnSavedEntity() {
        // Given
        Item input = new Item("Amber");
        when(itemRepository.save(input)).thenReturn(item1); // mock returning item1 as saved

        // When
        Item result = itemService.save(input);

        // Then
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getName()).isEqualTo("Granite"); // item1's name
        verify(itemRepository).save(input);
    }

    @Test
    @DisplayName("save delegates to the repository exactly once")
    void save_shouldCallRepositorySaveOnce() {
        // Given
        when(itemRepository.save(item1)).thenReturn(item1);

        // When
        itemService.save(item1);

        // Then
        verify(itemRepository, times(1)).save(item1);
    }

    // -------------------------------------------------------------------------
    // deleteById cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById removes the item when it exists")
    void deleteById_shouldDeleteItemWhenItExists() {
        // Given
        UUID id = UUID.randomUUID();
        when(itemRepository.existsById(id)).thenReturn(true);

        // When
        itemService.deleteById(id);

        // Then
        verify(itemRepository).existsById(id);
        verify(itemRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteById calls deleteById on the repository exactly once")
    void deleteById_shouldCallRepositoryDeleteByIdOnce() {
        // Given
        UUID id = UUID.randomUUID();
        when(itemRepository.existsById(id)).thenReturn(true);

        // When
        itemService.deleteById(id);

        // Then
        verify(itemRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deleteById throws ResponseStatusException when the item does not exist")
    void deleteById_shouldThrowResponseStatusExceptionWhenItemDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(itemRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> itemService.deleteById(nonExistentId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(String.valueOf(nonExistentId));
    }

    @Test
    @DisplayName("deleteById never calls repository delete when the item does not exist")
    void deleteById_shouldNeverCallDeleteWhenItemDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(itemRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> itemService.deleteById(nonExistentId))
                .isInstanceOf(ResponseStatusException.class);

        verify(itemRepository, never()).deleteById(any());
    }
}
