package cat.itacademy.blackjack.service;

import cat.itacademy.blackjack.dto.PlayerDTO;
import cat.itacademy.blackjack.dto.UpdatePlayerDTO;
import cat.itacademy.blackjack.exception.PlayerNotFoundException;
import cat.itacademy.blackjack.mapper.PlayerMapper;
import cat.itacademy.blackjack.model.GameResult;
import cat.itacademy.blackjack.model.Player;
import cat.itacademy.blackjack.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<PlayerDTO> updatePlayerName(Long playerId, UpdatePlayerDTO updatePlayerDTO) {
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .flatMap(p -> {
                    p.setName(updatePlayerDTO.name());
                    return playerRepository.save(p);
                })
                .map(PlayerMapper::toDto);
    }

    @Override
    public Flux<PlayerDTO> getPlayersRanking() {
        return playerRepository.findAllByOrderByNumberOfWinsDescNumberOfTiesDescNumberOfLossesAsc()
                .map(PlayerMapper::toDto);
    }

    @Override
    public Mono<Player> getOrCreatePlayer(String playerName) {
        return playerRepository.findByNameIgnoreCase(playerName)
                .switchIfEmpty(Mono.defer(() ->
                        playerRepository.save(new Player(playerName))
                ));
    }

    @Override
    public Mono<Void> updateStats(Long playerId, GameResult result) {
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .flatMap(p -> {
                    switch (result) {
                        case PLAYER_WINS -> p.win();
                        case TIE -> p.tie();
                        case PLAYER_LOSES -> p.lose();
                    }
                    return playerRepository.save(p);
                })
                .then();
    }
}
