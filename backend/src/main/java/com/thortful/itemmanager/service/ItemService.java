package com.thortful.itemmanager.service;

import com.thortful.itemmanager.exception.ItemNotFoundException;
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

    public List<Item> saveAll(List<String> names) {
        List<Item> items = names.stream()
                .map(Item::new)
                .toList();
        return itemRepository.saveAll(items);
    }

    public void deleteById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        itemRepository.deleteById(id);
    }
}
