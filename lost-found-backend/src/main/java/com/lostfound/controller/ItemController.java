
package com.lostfound.controller;

import com.lostfound.model.ItemRequest;
import com.lostfound.model.ItemResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final JdbcTemplate jdbc;

    public ItemController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/{status}")
    public List<ItemResponse> getItems(
            @PathVariable String status) {

        String selected = status.toUpperCase();

        if (!selected.equals("LOST")
                && !selected.equals("FOUND")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be LOST or FOUND");
        }

        return jdbc.query(
            """
            SELECT item_id, user_id, item_name, category,
                   description, location, item_date, status
            FROM items
            WHERE status = ?
            ORDER BY created_at DESC
            """,
            (rs, row) -> new ItemResponse(
                rs.getLong("item_id"),
                rs.getLong("user_id"),
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getDate("item_date") == null ? null
                    : rs.getDate("item_date").toLocalDate(),
                rs.getString("status")
            ),
            selected
        );
    }

    @PostMapping("/{status}")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(
            @PathVariable String status,
            @RequestParam Long userId,
            @Valid @RequestBody ItemRequest request) {

        String selected = status.toUpperCase();

        if (!selected.equals("LOST")
                && !selected.equals("FOUND")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be LOST or FOUND");
        }

        Integer userExists = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE user_id = ?",
                Integer.class, userId);

        if (userExists == null || userExists == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found");
        }

        Date date = request.itemDate() == null ? null
                : Date.valueOf(request.itemDate());

        jdbc.update(
            """
            INSERT INTO items
            (user_id, item_name, category, description,
             location, item_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """,
            userId,
            request.itemName().trim(),
            request.category().trim(),
            request.description(),
            request.location().trim(),
            date,
            selected
        );

        Long id = jdbc.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class);

        return jdbc.queryForObject(
            """
            SELECT item_id, user_id, item_name, category,
                   description, location, item_date, status
            FROM items WHERE item_id = ?
            """,
            (rs, row) -> new ItemResponse(
                rs.getLong("item_id"),
                rs.getLong("user_id"),
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getDate("item_date") == null ? null
                    : rs.getDate("item_date").toLocalDate(),
                rs.getString("status")
            ),
            id
        );
    }
}
