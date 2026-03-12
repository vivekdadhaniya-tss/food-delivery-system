package com.fooddeliveryapp.service;

import com.fooddeliveryapp.model.Category;
import com.fooddeliveryapp.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuService {

    // --- Category Management ---
    Category addCategory(String name);
    void renameCategory(String categoryId, String newName);
    void toggleCategoryStatus(String categoryId, boolean activate);
    List<Category> getAllCategories();
    List<Category> getActiveCategories();

    // --- MenuItem Management ---
    MenuItem addMenuItem(String name, double price, String categoryId);
    void updateMenuItem(String itemId, String name, double price, String categoryId);
    void toggleMenuItemAvailability(String itemId, boolean available);
    void removeMenuItem(String itemId); // Soft delete

    Optional<MenuItem> getMenuItemById(String itemId);
    List<MenuItem> getAllMenuItems();
    List<MenuItem> getAvailableMenuItemsByCategory(String categoryId);
    List<MenuItem> searchMenuItemsByName(String keyword);
}