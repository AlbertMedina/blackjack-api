package cat.itacademy.blackjack.service;

import cat.itacademy.blackjack.dto.*;
import cat.itacademy.blackjack.exception.GameNotFoundException;
import cat.itacademy.blackjack.exception.InvalidGameActionException;
import cat.itacademy.blackjack.mapper.GameMapper;
import cat.itacademy.blackjack.model.Game;
import cat.itacademy.blackjack.model.GameAction;
import cat.itacademy.blackjack.model.GameState;
import cat.itacademy.blackjack.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    private final PlayerService playerService;
    private final GameRepository gameRepository;

    public GameServiceImpl(PlayerService playerService, GameRepository gameRepository) {
        this.playerService = playerService;
        this.gameRepository = gameRepository;
    }

    @Override
    public Mono<GameDTO> createGame(CreateGameDTO createGameDTO) {
        return playerService.getOrCreatePlayer(createGameDTO.playerName())
                .flatMap(p -> {
                    Game newGame = Game.newGame(p.getId());
                    return gameRepository.save(newGame);
                })
                .map(GameMapper::toDto);
    }

    @Override
    public Mono<GameDTO> getGameInfo(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException(id)))
                .map(GameMapper::toDto);
    }

    @Override
    public Mono<GameDTO> playGame(String id, PlayGameDTO playGameDTO) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException(id)))
                .flatMap(game -> applyAction(game, playGameDTO.action()))
                .flatMap(this::saveGameAndHandleStats)
                .map(GameMapper::toDto);
    }

    private Mono<Game> saveGameAndHandleStats(Game game) {
        return gameRepository.save(game)
                .flatMap(g -> {
                    if (g.getState() == GameState.FINISHED) {
                        return playerService.updateStats(g.getPlayerId(), g.getResult())
                                .thenReturn(g)
                                .doOnError(e -> log.error("EVENTUAL CONSISTENCY ERROR: Failed updating player {} stats: {}", g.getPlayerId(), e.getMessage()))
                                .onErrorReturn(g);
                    }
                    return Mono.just(g);
                });
    }

    @Override
    public Mono<Void> removeGame(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException(id)))
                .flatMap(gameRepository::delete);
    }

    private Mono<Game> applyAction(Game g, GameAction action) {
        if (action == null) {
            return Mono.error(new InvalidGameActionException(null, "Action cannot be null"));
        }

        return switch (action) {
            case HIT -> Mono.fromRunnable(g::hit).thenReturn(g);
            case STAND -> Mono.fromRunnable(g::stand).thenReturn(g);
        };
    }
}
