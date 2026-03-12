package com.fooddeliveryapp.service.Impl;

import com.fooddeliveryapp.exception.FoodDeliveryException;
import com.fooddeliveryapp.model.Category;
import com.fooddeliveryapp.model.MenuItem;
import com.fooddeliveryapp.repository.CategoryRepository;
import com.fooddeliveryapp.repository.MenuItemRepository;
import com.fooddeliveryapp.service.MenuService;
import com.fooddeliveryapp.type.ErrorType;
import com.fooddeliveryapp.type.IdType;
import com.fooddeliveryapp.util.IdGenerator;
import com.fooddeliveryapp.util.InputUtil;

import java.util.List;
import java.util.Optional;

public class MenuServiceImpl implements MenuService {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuServiceImpl(CategoryRepository categoryRepository, MenuItemRepository menuItemRepository) {
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    // --- Category Management ---
    @Override
    public Category addCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Category name already exists");
        }
        Category category = new Category(IdGenerator.generate(IdType.CATEGORY), name);
        return categoryRepository.save(category);
    }

    @Override
    public void renameCategory(String categoryId, String newName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Category not found"));
        category.rename(newName);
        categoryRepository.save(category);
    }

    @Override
    public void toggleCategoryStatus(String categoryId, boolean activate) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Category not found"));
        if (activate) category.activate();
        else category.deactivate();
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findAllActive();
    }

    // --- MenuItem Management ---
    @Override
    public MenuItem addMenuItem(String name, double price, String categoryId) {
        InputUtil.requireNonBlank(name, "Name");
        InputUtil.requirePositive(price, "Price");

        if (!categoryRepository.existsById(categoryId)) {
            throw new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Category does not exist");
        }

        // Generate ID LAST
        String id = IdGenerator.generate(IdType.MENU_ITEM);
        MenuItem item = new MenuItem(id, name, price, categoryId);
        return menuItemRepository.save(item);
    }

    @Override
    public void updateMenuItem(String itemId, String name, double price, String categoryId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Menu item not found"));
        if (!categoryRepository.existsById(categoryId)) {
            throw new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Category does not exist");
        }
        item.rename(name);
        item.updatePrice(price);
        item.changeCategory(categoryId);
        menuItemRepository.save(item);
    }

    @Override
    public void toggleMenuItemAvailability(String itemId, boolean available) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new FoodDeliveryException(ErrorType.RESOURCE_NOT_FOUND, "Menu item not found"));
        item.changeAvailability(available);
        menuItemRepository.save(item);
    }

    @Override
    public void removeMenuItem(String itemId) {
        menuItemRepository.deleteById(itemId);
    }

    @Override
    public Optional<MenuItem> getMenuItemById(String itemId) {
        return menuItemRepository.findById(itemId);
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public List<MenuItem> getAvailableMenuItemsByCategory(String categoryId) {
        return menuItemRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<MenuItem> searchMenuItemsByName(String keyword) {
        return menuItemRepository.findAllActive().stream()
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}