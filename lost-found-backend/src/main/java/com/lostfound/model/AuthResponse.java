
package com.lostfound.model;

public record AuthResponse(
    boolean success,
    String message,
    Long userId,
    String fullName,
    String email,
    String role
) {}
