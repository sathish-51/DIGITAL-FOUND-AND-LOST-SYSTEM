package com.lostfound.model;

import java.time.LocalDate;

public record ItemResponse(
        Long itemId,
        Long userId,
        String itemName,
        String category,
        String description,
        String location,
        LocalDate itemDate,
        String status
) {}
