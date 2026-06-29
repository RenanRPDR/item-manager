package com.thortful.itemmanager.repository;

import java.util.UUID;

import com.thortful.itemmanager.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    Page<Item> findByNameContainingIgnoreCase(String nameFragment, Pageable pageable);
}
