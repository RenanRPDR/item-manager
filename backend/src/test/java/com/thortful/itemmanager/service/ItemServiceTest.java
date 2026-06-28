package com.thortful.itemmanager.service;

import com.thortful.itemmanager.exception.ItemNotFoundException;
import com.thortful.itemmanager.model.Item;
import com.thortful.itemmanager.repository.ItemRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService — Unit Tests")
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    // -------------------------------------------------------------------------
    // findAll cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll returns the full list from the repository")
    void findAll_shouldReturnAllItems() {
        List<Item> items = List.of(new Item("Granite"), new Item("Marble"));
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.findAll();

        assertThat(result).hasSize(2).containsExactlyElementsOf(items);
        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("findAll returns an empty list when the repository is empty")
    void findAll_shouldReturnEmptyListWhenNoItems() {
        when(itemRepository.findAll()).thenReturn(List.of());

        List<Item> result = itemService.findAll();

        assertThat(result).isEmpty();
        verify(itemRepository).findAll();
    }

    // -------------------------------------------------------------------------
    // searchByName cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("searchByName forwards the fragment and pageable to the repository")
    void searchByName_shouldForwardFragmentAndPageableToRepository() {
        String fragment = "stone";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> expectedPage = new PageImpl<>(List.of(new Item("Granite Stone")));

        when(itemRepository.findByNameContainingIgnoreCase(fragment, pageable))
                .thenReturn(expectedPage);

        Page<Item> result = itemService.searchByName(fragment, pageable);

        assertThat(result).isEqualTo(expectedPage);
        verify(itemRepository).findByNameContainingIgnoreCase(fragment, pageable);
    }

    @Test
    @DisplayName("searchByName returns an empty page when no items match the fragment")
    void searchByName_shouldReturnEmptyPageWhenNoMatch() {
        String fragment = "zzz";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> emptyPage = Page.empty(pageable);

        when(itemRepository.findByNameContainingIgnoreCase(fragment, pageable))
                .thenReturn(emptyPage);

        Page<Item> result = itemService.searchByName(fragment, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("searchByName uses the correct page number and page size")
    void searchByName_shouldUseCorrectPaginationParameters() {
        Pageable pageable = PageRequest.of(2, 5);
        Page<Item> page = new PageImpl<>(List.of(), pageable, 0);

        when(itemRepository.findByNameContainingIgnoreCase("rock", pageable))
                .thenReturn(page);

        itemService.searchByName("rock", pageable);

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
        Item input = new Item("Amber");
        Item saved = new Item("Amber");
        saved.setId(1L);

        when(itemRepository.save(input)).thenReturn(saved);

        Item result = itemService.save(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Amber");
        verify(itemRepository).save(input);
    }

    @Test
    @DisplayName("save delegates to the repository exactly once")
    void save_shouldCallRepositorySaveOnce() {
        Item item = new Item("Obsidian");
        when(itemRepository.save(item)).thenReturn(item);

        itemService.save(item);

        verify(itemRepository, times(1)).save(item);
    }

    // -------------------------------------------------------------------------
    // deleteById cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById removes the item when it exists")
    void deleteById_shouldDeleteItemWhenItExists() {
        Long id = 1L;
        when(itemRepository.existsById(id)).thenReturn(true);

        itemService.deleteById(id);

        verify(itemRepository).existsById(id);
        verify(itemRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteById calls deleteById on the repository exactly once")
    void deleteById_shouldCallRepositoryDeleteByIdOnce() {
        Long id = 5L;
        when(itemRepository.existsById(id)).thenReturn(true);

        itemService.deleteById(id);

        verify(itemRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deleteById throws ItemNotFoundException when the item does not exist")
    void deleteById_shouldThrowItemNotFoundExceptionWhenItemDoesNotExist() {
        Long nonExistentId = 999L;
        when(itemRepository.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> itemService.deleteById(nonExistentId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(String.valueOf(nonExistentId));
    }

    @Test
    @DisplayName("deleteById never calls repository delete when the item does not exist")
    void deleteById_shouldNeverCallDeleteWhenItemDoesNotExist() {
        Long nonExistentId = 999L;
        when(itemRepository.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> itemService.deleteById(nonExistentId))
                .isInstanceOf(ItemNotFoundException.class);

        verify(itemRepository, never()).deleteById(any());
    }
}
