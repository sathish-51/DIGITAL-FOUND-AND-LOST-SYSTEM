package com.lostfound.desktop.api;
public record AuthResult(
        boolean success,
        String message,
        Long userId,
        String fullName,
        String email,
        String role
) {}
