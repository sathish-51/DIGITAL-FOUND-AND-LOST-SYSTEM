
package com.lostfound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserDashboardController {

    private final JdbcTemplate jdbc;

    public UserDashboardController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ================= MY CLAIMS =================

    @GetMapping("/claims/my")
    public List<Map<String, Object>> myClaims(
            @RequestParam Long userId) {

        return jdbc.queryForList("""
            SELECT
                c.claim_id AS claimId,
                c.claimant_id AS userId,
                c.found_item_id AS itemId,
                i.item_name AS itemName,
                c.proof_description AS claimMessage,
                c.status AS status,
                c.review_notes AS reviewNotes,
                c.created_at AS createdAt,
                c.reviewed_at AS reviewedAt
            FROM claims c
            JOIN items i ON i.item_id = c.found_item_id
            WHERE c.claimant_id = ?
            ORDER BY c.created_at DESC
            """, userId);
    }

    // ================= SUBMIT CLAIM =================

    @PostMapping("/claims")
    @Transactional
    public Map<String, Object> createClaim(
            @RequestParam Long userId,
            @RequestParam Long itemId,
            @RequestBody Map<String, String> body) {

        String message = body.get("claimMessage");

        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Claim message cannot be empty");
        }

        // Check whether the user exists.
        Integer userCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE user_id = ?",
                Integer.class,
                userId);

        if (userCount == null || userCount == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found");
        }

        // Check whether the item exists and is found.
        List<String> statuses = jdbc.query(
                "SELECT status FROM items WHERE item_id = ?",
                (rs, rowNum) -> rs.getString("status"),
                itemId);

        if (statuses.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Item not found");
        }

        if (!"FOUND".equalsIgnoreCase(statuses.get(0))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only found items can be claimed");
        }

        // Prevent the same user from claiming the same item twice.
        Integer existingClaim = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM claims
            WHERE claimant_id = ?
              AND found_item_id = ?
              AND status <> 'REJECTED'
            """, Integer.class, userId, itemId);

        if (existingClaim != null && existingClaim > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already submitted a claim for this item");
        }

        // Insert using the actual database column names.
        jdbc.update("""
            INSERT INTO claims
                (claimant_id, found_item_id, proof_description, status)
            VALUES (?, ?, ?, 'PENDING')
            """,
            userId, itemId, message.trim());

        // Notify the claimant.
        jdbc.update("""
            INSERT INTO notifications (user_id, message, is_read)
            VALUES (?, ?, FALSE)
            """,
            userId,
            "Your claim for item ID " + itemId
                    + " has been submitted and is pending review.");

        return Map.of(
                "success", true,
                "message", "Claim submitted successfully",
                "status", "PENDING");
    }

    // ================= NOTIFICATIONS =================

    @GetMapping("/notifications")
    public List<Map<String, Object>> notifications(
            @RequestParam Long userId) {

        return jdbc.queryForList("""
            SELECT notification_id, message, is_read, created_at
            FROM notifications
            WHERE user_id = ?
            ORDER BY created_at DESC
            """, userId);
    }

    // ================= PROFILE =================

    @GetMapping("/profile")
    public Map<String, Object> profile(
            @RequestParam Long userId) {

        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT user_id, full_name, email, role, account_status
            FROM users
            WHERE user_id = ?
            """, userId);

        if (rows.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found");
        }

        return rows.get(0);
    }
}
