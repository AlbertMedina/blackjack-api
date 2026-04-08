package cat.itacademy.blackjack.controller;

import cat.itacademy.blackjack.dto.CreateGameDTO;
import cat.itacademy.blackjack.dto.GameDTO;
import cat.itacademy.blackjack.dto.PlayGameDTO;
import cat.itacademy.blackjack.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Create new Blackjack game")
    @ApiResponse(responseCode = "201", description = "Game created successfully")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GameDTO> createGame(@RequestBody @Valid CreateGameDTO createGameDTO) {
        return gameService.createGame(createGameDTO);
    }

    @Operation(summary = "Play an existing game")
    @ApiResponse(responseCode = "200", description = "Played successfully")
    @PostMapping("/{id}/play")
    public Mono<GameDTO> playGame(@PathVariable String id, @RequestBody @Valid PlayGameDTO playGameDTO) {
        return gameService.playGame(id, playGameDTO);
    }

    @Operation(summary = "Get game information")
    @ApiResponse(responseCode = "200", description = "Game info retrieved successfully")
    @GetMapping("/{id}")
    public Mono<GameDTO> getGameInfoById(@PathVariable String id) {
        return gameService.getGameInfo(id);
    }

    @Operation(summary = "Remove an existing Blackjack game")
    @ApiResponse(responseCode = "204", description = "Game removed successfully")
    @DeleteMapping("/{id}")
    public Mono<Void> removeGame(@PathVariable String id) {
        return gameService.removeGame(id);
    }
}
