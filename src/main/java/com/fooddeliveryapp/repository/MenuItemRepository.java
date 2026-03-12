package com.fooddeliveryapp.repository;

import com.fooddeliveryapp.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    MenuItem save(MenuItem item);
    Optional<MenuItem> findById(String id);
    Optional<MenuItem> findByName(String name);
    List<MenuItem> findAll();
    List<MenuItem> findAllActive();
    List<MenuItem> findAllInactive();
    List<MenuItem> findByCategoryId(String categoryId);
    void deleteById(String id);
    boolean existsById(String id);
    boolean existsByName(String name);
}
