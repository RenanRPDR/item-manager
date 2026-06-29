package com.thortful.itemmanager.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.thortful.itemmanager.model.Item;
import com.thortful.itemmanager.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Page<Item> searchByName(String nameFragment, Pageable pageable) {
        return itemRepository.findByNameContainingIgnoreCase(nameFragment, pageable);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(UUID id) {
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }
}
