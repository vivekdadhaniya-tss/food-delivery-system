package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.MenuItem;
import com.fooddeliveryapp.model.Category;
import com.fooddeliveryapp.repository.MenuItemRepository;
import com.fooddeliveryapp.repository.CategoryRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuItemRepository implements MenuItemRepository {

    private final Map<String, MenuItem> store = new HashMap<>();
    private final Map<String, String> nameIndex = new HashMap<>();  // store active record
    private final CategoryRepository categoryRepository;

    public InMemoryMenuItemRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public MenuItem save(MenuItem item) {
        if (item == null) throw new IllegalArgumentException("MenuItem cannot be null");

        store.put(item.getId(), item);

        if (item.getName() != null && !item.getName().isBlank()) {
            nameIndex.put(item.getName().toLowerCase(), item.getId());
        }
        return item;
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<MenuItem> findByName(String name) {
        if (name == null || name.isBlank()) return Optional.empty();
        String id = nameIndex.get(name.toLowerCase());
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<MenuItem> findAllActive() {
        return store.values().stream()
                .filter(MenuItem::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> findAllInactive() {
        return store.values().stream()
                .filter(item -> !item.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> findByCategoryId(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) return List.of();

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty() || !categoryOpt.get().isActive()) {
            return List.of(); // Category not found or inactive
        }

        return store.values().stream()
                .filter(item -> categoryId.equals(item.getCategoryId()) && item.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        MenuItem item = store.get(id);
        if (item != null) {
            item.changeAvailability(false); // soft delete
            if (item.getName() != null) {
                nameIndex.remove(item.getName().toLowerCase());
            }
        }
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) return false;

        MenuItem item = store.get(id);
        if (item == null || !item.isAvailable()) return false;

        // check if the category is active
        return categoryRepository.findById(item.getCategoryId())
                .map(Category::isActive)
                .orElse(false);
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.isBlank()) return false;
        return nameIndex.containsKey(name.toLowerCase());
    }
}