package com.fooddeliveryapp.repository;

import com.fooddeliveryapp.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(int id);

    Optional<Restaurant> findByExactName(String name);

    List<Restaurant> searchByName(String keyword);

    List<Restaurant> findAll();

    void deleteById(int id);

    boolean existsById(int id);
}