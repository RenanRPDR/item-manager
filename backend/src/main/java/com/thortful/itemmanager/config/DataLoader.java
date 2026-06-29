package com.thortful.itemmanager.config;

import com.thortful.itemmanager.model.Item;
import com.thortful.itemmanager.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public CommandLineRunner seedDatabase(ItemRepository itemRepository) {
        return args -> {
            if (itemRepository.count() > 0) {
                log.info("Database already seeded with {} items. Skipping.", itemRepository.count());
                return;
            }

            log.info("Seeding database from items.csv...");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new ClassPathResource("items.csv").getInputStream()))) {

                List<Item> items = br.lines()
                        .skip(1) // skip the "name" header
                        .filter(line -> !line.trim().isEmpty())
                        .map(String::trim)
                        .map(Item::new)
                        .toList();

                itemRepository.saveAll(items);
            } catch (Exception e) {
                log.error("Failed to load items from CSV", e);
                return;
            }
            log.info("Successfully seeded {} items into the database.", itemRepository.count());
        };
    }
}
