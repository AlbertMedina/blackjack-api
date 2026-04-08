package cat.itacademy.blackjack.controller;

import cat.itacademy.blackjack.dto.PlayerDTO;
import cat.itacademy.blackjack.dto.UpdatePlayerDTO;
import cat.itacademy.blackjack.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(summary = "Update player name")
    @ApiResponse(responseCode = "200", description = "Player name updated successfully")
    @PutMapping("/{id}")
    public Mono<PlayerDTO> updatePlayerName(@PathVariable Long id, @RequestBody @Valid UpdatePlayerDTO playerDTORequest) {
        return playerService.updatePlayerName(id, playerDTORequest);
    }

    @Operation(summary = "Get the ranking of players by number of wins")
    @ApiResponse(responseCode = "200", description = "Player ranking retrieved successfully")
    @GetMapping("/ranking")
    public Flux<PlayerDTO> getPlayersRanking() {
        return playerService.getPlayersRanking();
    }
}
