package com.api.domain.user.repository;

import com.api.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User create(User user);
}
