package cat.itacademy.blackjack.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGameDTO(
        @NotBlank String playerName
) {
}
