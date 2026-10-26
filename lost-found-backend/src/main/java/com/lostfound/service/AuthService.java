
package com.lostfound.service;

import com.lostfound.model.AuthResponse;
import com.lostfound.model.LoginRequest;
import com.lostfound.model.RegisterRequest;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuthResponse register(RegisterRequest request) {

        String name = request.fullName().trim();
        String email = request.email().trim().toLowerCase();

        if (name.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name is required");
        }

        if (request.password().getBytes(
                java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must not exceed 72 UTF-8 bytes");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        try {
            jdbcTemplate.update(
                """
                INSERT INTO users
                (full_name, email, password_hash, role, account_status)
                VALUES (?, ?, ?, 'USER', 'ACTIVE')
                """,
                name, email, passwordHash
            );
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists");
        }

        Long userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE email = ?",
                Long.class,
                email
        );

        return new AuthResponse(
                true,
                "Registration successful",
                userId,
                name,
                email,
                "USER"
        );
    }

    public AuthResponse login(LoginRequest request) {

        String email = request.email().trim().toLowerCase();

        var users = jdbcTemplate.query(
            """
            SELECT user_id, full_name, email, password_hash,
                   role, account_status
            FROM users
            WHERE email = ?
            """,
            (rs, rowNum) -> new Object[] {
                rs.getLong("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("account_status")
            },
            email
        );

        if (users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        Object[] user = users.get(0);

        String storedHash = (String) user[3];

        if (!passwordEncoder.matches(
                request.password(), storedHash)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        if (!"ACTIVE".equals(user[5])) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This account is not active");
        }

        return new AuthResponse(
                true,
                "Login successful",
                (Long) user[0],
                (String) user[1],
                (String) user[2],
                (String) user[4]
        );
    }
}
