package cat.itacademy.blackjack.controller;

import cat.itacademy.blackjack.dto.*;
import cat.itacademy.blackjack.exception.GameNotFoundException;
import cat.itacademy.blackjack.model.*;
import cat.itacademy.blackjack.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GameService gameService;

    @Test
    void createGame_shouldReturnCreatedGame() {
        CreateGameDTO request = new CreateGameDTO("PlayerName");

        GameDTO response = new GameDTO(
                "1",
                1L,
                new HandDTO(
                        new ArrayList<>(List.of(
                                new CardDTO(Rank.EIGHT, Suit.CLUBS),
                                new CardDTO(Rank.ACE, Suit.DIAMONDS)
                        ))
                ),
                new HandDTO(
                        new ArrayList<>(List.of(
                                new CardDTO(Rank.FOUR, Suit.CLUBS),
                                new CardDTO(Rank.QUEEN, Suit.HEARTS)
                        ))
                ),
                GameState.IN_PROGRESS,
                GameResult.UNDETERMINED
        );

        when(gameService.createGame(request))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GameDTO.class)
                .value(g ->
                        assertEquals("1", g.id())
                );
    }

    @Test
    void playGame_shouldReturnUpdatedGameForValidAction() {
        PlayGameDTO request = new PlayGameDTO(GameAction.STAND);

        GameDTO response = new GameDTO(
                "1",
                1L,
                new HandDTO(
                        new ArrayList<>(List.of(
                                new CardDTO(Rank.EIGHT, Suit.CLUBS),
                                new CardDTO(Rank.ACE, Suit.DIAMONDS)
                        ))
                ),
                new HandDTO(
                        new ArrayList<>(List.of(
                                new CardDTO(Rank.FOUR, Suit.CLUBS),
                                new CardDTO(Rank.QUEEN, Suit.HEARTS)
                        ))
                ),
                GameState.IN_PROGRESS,
                GameResult.UNDETERMINED
        );

        when(gameService.playGame("1", request))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/games/{id}/play", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GameDTO.class)
                .value(g ->
                        assertEquals("1", g.id())
                );
    }

    @Test
    void playGame_shouldReturnNotFoundWhenGameDoesNotExist() {
        PlayGameDTO request = new PlayGameDTO(GameAction.HIT);

        when(gameService.playGame("1", request))
                .thenThrow(new GameNotFoundException("1"));

        webTestClient.post()
                .uri("/games/{id}/play", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getGameInfoById_shouldReturnGameWhenExists() {
        GameDTO response = new GameDTO(
                "1",
                1L,
                new HandDTO(
                        new ArrayList<>(List.of(
                                new CardDTO(Rank.EIGHT, Suit.CLUBS),
                                new CardDTO(Rank.ACE, Suit.DIAMONDS)
                        ))
                ),
                new HandDTO(
                        new ArrayList<>(List.of(
                                new CardDTO(Rank.FOUR, Suit.CLUBS),
                                new CardDTO(Rank.QUEEN, Suit.HEARTS)
                        ))
                ),
                GameState.IN_PROGRESS,
                GameResult.UNDETERMINED
        );

        when(gameService.getGameInfo("1"))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/games/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GameDTO.class)
                .value(g ->
                        assertEquals("1", g.id())
                );
    }

    @Test
    void getGameInfoById_shouldReturnNotFoundWhenGameDoesNotExist() {
        when(gameService.getGameInfo("1"))
                .thenThrow(new GameNotFoundException("1"));

        webTestClient.get()
                .uri("/games/{id}", "1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void removeGame_shouldReturnOkWhenGameDeleted() {
        when(gameService.removeGame("1"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/games/{id}", "1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void removeGame_shouldReturnNotFoundWhenGameDoesNotExist() {
        when(gameService.removeGame("1"))
                .thenThrow(new GameNotFoundException("1"));

        webTestClient.delete()
                .uri("/games/{id}", "1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
