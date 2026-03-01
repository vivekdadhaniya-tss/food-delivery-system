package com.fooddeliveryapp.repository;

import com.fooddeliveryapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}