package cat.itacademy.blackjack.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePlayerDTO(
        @NotBlank String name
) {
}
