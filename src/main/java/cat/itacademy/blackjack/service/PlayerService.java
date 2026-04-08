package cat.itacademy.blackjack.service;

import cat.itacademy.blackjack.dto.PlayerDTO;
import cat.itacademy.blackjack.dto.UpdatePlayerDTO;
import cat.itacademy.blackjack.model.GameResult;
import cat.itacademy.blackjack.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerService {

    Mono<PlayerDTO> updatePlayerName(Long playerId, UpdatePlayerDTO playerDTO);

    Mono<Player> getOrCreatePlayer(String playerName);

    Flux<PlayerDTO> getPlayersRanking();

    Mono<Void> updateStats(Long playerId, GameResult gameResult);
}
