
package com.lostfound.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ItemRequest(
        @NotBlank @Size(max = 120) String itemName,
        @NotBlank @Size(max = 80) String category,
        @Size(max = 3000) String description,
        @NotBlank @Size(max = 200) String location,
        LocalDate itemDate
) {}
