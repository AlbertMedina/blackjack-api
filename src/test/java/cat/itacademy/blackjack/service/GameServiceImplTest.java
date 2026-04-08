package cat.itacademy.blackjack.service;

import cat.itacademy.blackjack.dto.CreateGameDTO;
import cat.itacademy.blackjack.dto.GameDTO;
import cat.itacademy.blackjack.dto.PlayGameDTO;
import cat.itacademy.blackjack.exception.GameNotFoundException;
import cat.itacademy.blackjack.exception.InvalidGameActionException;
import cat.itacademy.blackjack.model.*;
import cat.itacademy.blackjack.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private PlayerService playerService;

    @Test
    void createGame_shouldCreateAndReturnGame() {
        Player player = new Player("PlayerName");

        when(playerService.getOrCreatePlayer("PlayerName"))
                .thenReturn(Mono.just(player));

        Game game = Game.newGame(1L);

        when(gameRepository.save(any(Game.class)))
                .thenReturn(Mono.just(game));

        CreateGameDTO request = new CreateGameDTO("PlayerName");

        Mono<GameDTO> result = gameService.createGame(request);

        StepVerifier.create(result)
                .assertNext(gameDTO -> {
                    assertEquals(1L, gameDTO.playerId());
                })
                .verifyComplete();
    }

    @Test
    void getGameInfo_shouldReturnGameWhenGameExists() {
        Game game = Game.newGame(1L);

        when(gameRepository.findById("1"))
                .thenReturn(Mono.just(game));

        Mono<GameDTO> result = gameService.getGameInfo("1");

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(1L, dto.playerId());
                })
                .verifyComplete();
    }

    @Test
    void getGameInfo_shouldThrowNotFoundWhenGameDoesNotExist() {
        when(gameRepository.findById("1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(gameService.getGameInfo("1"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playGame_shouldApplyHitActionAndReturnUpdatedGame() {
        Game game = Game.newGame(1L);

        when(gameRepository.findById("1"))
                .thenReturn(Mono.just(game));

        when(gameRepository.save(any(Game.class)))
                .thenAnswer(inv -> Mono.just(game));

        PlayGameDTO request = new PlayGameDTO(GameAction.HIT);

        Mono<GameDTO> result = gameService.playGame("1", request);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(3, dto.playerHand().cards().size());
                })
                .verifyComplete();

        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void playGame_shouldApplyStandActionAndReturnUpdatedGame() {
        Game game = Game.newGame(1L);

        when(gameRepository.findById("1"))
                .thenReturn(Mono.just(game));

        when(gameRepository.save(any(Game.class)))
                .thenAnswer(inv -> Mono.just(game));

        PlayGameDTO request = new PlayGameDTO(GameAction.STAND);

        Mono<GameDTO> result = gameService.playGame("1", request);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(2, dto.playerHand().cards().size());
                    assertEquals(GameState.FINISHED, dto.state());
                    assertNotEquals(GameResult.UNDETERMINED, dto.result());
                })
                .verifyComplete();

        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void playGame_shouldThrowNotFoundWhenGameDoesNotExist() {
        when(gameRepository.findById("1"))
                .thenReturn(Mono.empty());

        PlayGameDTO request = new PlayGameDTO(GameAction.HIT);

        StepVerifier.create(gameService.playGame("1", request))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playGame_shouldThrowInvalidGameActionForUnsupportedAction() {
        Game game = Game.newGame(1L);

        when(gameRepository.findById("1"))
                .thenReturn(Mono.just(game));

        PlayGameDTO request = new PlayGameDTO(null);

        StepVerifier.create(gameService.playGame("1", request))
                .expectError(InvalidGameActionException.class)
                .verify();
    }

    @Test
    void removeGame_shouldDeleteGameWhenGameExists() {
        Game game = Game.newGame(1L);

        when(gameRepository.findById("1"))
                .thenReturn(Mono.just(game));

        when(gameRepository.delete(game))
                .thenReturn(Mono.empty());

        StepVerifier.create(gameService.removeGame("1"))
                .verifyComplete();

        verify(gameRepository).delete(game);
    }

    @Test
    void removeGame_shouldThrowNotFoundWhenGameDoesNotExist() {
        when(gameRepository.findById("1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(gameService.removeGame("1"))
                .expectError(GameNotFoundException.class)
                .verify();
    }
}
