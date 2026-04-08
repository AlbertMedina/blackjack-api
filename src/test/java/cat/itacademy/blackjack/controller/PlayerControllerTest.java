package cat.itacademy.blackjack.controller;

import cat.itacademy.blackjack.dto.PlayerDTO;
import cat.itacademy.blackjack.dto.UpdatePlayerDTO;
import cat.itacademy.blackjack.exception.PlayerNotFoundException;
import cat.itacademy.blackjack.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebFluxTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PlayerService playerService;

    @Test
    void updatePlayerName_shouldUpdateNameProperlyForExistingPlayer() {
        UpdatePlayerDTO request = new UpdatePlayerDTO("PlayerName");

        PlayerDTO response = new PlayerDTO(
                1L,
                "PlayerName",
                2,
                1,
                0
        );

        when(playerService.updatePlayerName(1L, request))
                .thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/player/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PlayerDTO.class)
                .value(p ->
                        assertEquals("PlayerName", p.name())
                );
    }

    @Test
    void updatePlayerName_shouldReturnNotFoundWhenPlayerDoesNotExist() {
        UpdatePlayerDTO request = new UpdatePlayerDTO("PlayerName");

        when(playerService.updatePlayerName(1L, request))
                .thenThrow(new PlayerNotFoundException(1L));

        webTestClient.put()
                .uri("/player/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getPlayersRanking_shouldReturnOrderedPlayers() {
        List<PlayerDTO> mockPlayers = List.of(
                new PlayerDTO(1L, "Player1", 5, 1, 2),
                new PlayerDTO(2L, "Player2", 3, 2, 1),
                new PlayerDTO(3L, "Player3", 1, 0, 4)
        );

        when(playerService.getPlayersRanking())
                .thenReturn(Flux.fromIterable(mockPlayers));

        webTestClient.get()
                .uri("/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PlayerDTO.class)
                .value(players -> {
                    assertEquals(3, players.size());
                    assertEquals("Player1", players.get(0).name());
                    assertEquals("Player2", players.get(1).name());
                    assertEquals("Player3", players.get(2).name());
                });
    }
}