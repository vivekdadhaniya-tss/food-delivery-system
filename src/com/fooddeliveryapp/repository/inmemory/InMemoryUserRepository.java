package com.fooddeliveryapp.repository.inmemory;

import com.fooddeliveryapp.model.User;
import com.fooddeliveryapp.repository.UserRepository;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> usersById = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "User can't be null");

        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);

        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        if(id==null || id.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if(email==null || email.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public void deleteById(String id) {
        User removedUser = usersById.remove(id);
        if(removedUser!=null) {
            usersByEmail.remove(removedUser.getEmail());
        }
    }

    @Override
    public boolean existsById(String id) {
        return usersById.containsKey(id);
    }
}