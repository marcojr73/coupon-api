package com.api.domain.user.repository;

import com.api.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    User create(User user);
}
