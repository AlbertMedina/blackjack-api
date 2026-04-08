package cat.itacademy.blackjack.service;

import cat.itacademy.blackjack.dto.PlayerDTO;
import cat.itacademy.blackjack.dto.UpdatePlayerDTO;
import cat.itacademy.blackjack.exception.PlayerNotFoundException;
import cat.itacademy.blackjack.model.GameResult;
import cat.itacademy.blackjack.model.Player;
import cat.itacademy.blackjack.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Test
    void updatePlayerName_shouldUpdateNameWhenPlayerExists() {
        Player player = new Player("PlayerName");

        UpdatePlayerDTO request = new UpdatePlayerDTO("NewName");

        when(playerRepository.findById(1L))
                .thenReturn(Mono.just(player));

        when(playerRepository.save(player))
                .thenReturn(Mono.just(player));

        Mono<PlayerDTO> result = playerService.updatePlayerName(1L, request);

        StepVerifier.create(result)
                .assertNext(p -> {
                    assertEquals("NewName", p.name());
                })
                .verifyComplete();
    }

    @Test
    void updatePlayerName_shouldThrowExceptionWhenPlayerDoesNotExist() {
        UpdatePlayerDTO request = new UpdatePlayerDTO("NewName");

        when(playerRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(playerService.updatePlayerName(1L, request))
                .expectError(PlayerNotFoundException.class)
                .verify();
    }

    @Test
    void getPlayersRanking_shouldReturnPlayersOrderedByNumberOfWins() {
        Player player1 = new Player("Player1");
        player1.win();
        player1.win();

        Player player2 = new Player("Player2");
        player2.win();

        Player player3 = new Player("Player3");

        List<Player> players = List.of(player1, player2, player3);

        when(playerRepository.findAllByOrderByNumberOfWinsDescNumberOfTiesDescNumberOfLossesAsc())
                .thenReturn(Flux.fromIterable(players));

        Flux<PlayerDTO> result = playerService.getPlayersRanking();

        StepVerifier.create(result)
                .assertNext(p -> {
                    assertEquals("Player1", p.name());
                })
                .assertNext(p -> {
                    assertEquals("Player2", p.name());
                })
                .assertNext(p -> {
                    assertEquals("Player3", p.name());
                })
                .verifyComplete();
    }

    @Test
    void getOrCreatePlayer_shouldReturnExistingPlayerWhenPlayerExists() {
        Player player = new Player("PlayerName");

        when(playerRepository.findByNameIgnoreCase("PlayerName"))
                .thenReturn(Mono.just(player));

        Mono<Player> result = playerService.getOrCreatePlayer("PlayerName");

        StepVerifier.create(result)
                .assertNext(p -> {
                    assertEquals(player, p);
                })
                .verifyComplete();
    }

    @Test
    void getOrCreatePlayer_shouldCreateAndReturnPlayerWhenPlayerDoesNotExist() {
        Player newPlayer = new Player("PlayerName");

        when(playerRepository.findByNameIgnoreCase("PlayerName"))
                .thenReturn(Mono.empty());

        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(newPlayer));

        Mono<Player> result = playerService.getOrCreatePlayer("PlayerName");

        StepVerifier.create(result)
                .assertNext(p -> {
                    assertEquals("PlayerName", p.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateStats_shouldIncreaseWinsWhenPlayerWins() {
        Player player = new Player("PlayerName");

        when(playerRepository.findById(1L))
                .thenReturn(Mono.just(player));

        when(playerRepository.save(player))
                .thenReturn(Mono.just(player));

        StepVerifier.create(playerService.updateStats(1L, GameResult.PLAYER_WINS))
                .verifyComplete();

        assertEquals(1, player.getNumberOfWins());
    }

    @Test
    void updateStats_shouldThrowExceptionWhenPlayerDoesNotExist() {
        when(playerRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(playerService.updateStats(1L, GameResult.PLAYER_WINS))
                .expectError(PlayerNotFoundException.class)
                .verify();
    }
}
