package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.repository.UserRepository;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User findByEmail(String email) {
        return users.values()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(int id) {
        users.remove(id);
    }

    @Override
    public boolean existsById(int id) {
        return users.containsKey(id);
    }
}