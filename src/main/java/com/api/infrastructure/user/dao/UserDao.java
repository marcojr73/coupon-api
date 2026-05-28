package com.api.infrastructure.user.dao;

import com.api.domain.user.entity.User;
import com.api.domain.user.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDao implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .id(rs.getString("id"))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .password(rs.getString("password"))
            .build();

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, userRowMapper, email).stream().findFirst();
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT id, name, email, password FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, userRowMapper, id).stream().findFirst();
    }

    @Override
    public User create(User user) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO users (id, name, email, password) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, id, user.getName(), user.getEmail(), user.getPassword());
        return User.builder()
                .id(id)
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
