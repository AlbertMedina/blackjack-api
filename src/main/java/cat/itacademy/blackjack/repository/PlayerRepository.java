package cat.itacademy.blackjack.repository;

import cat.itacademy.blackjack.model.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends R2dbcRepository<Player, Long> {

    Mono<Player> findByNameIgnoreCase(String name);

    Flux<Player> findAllByOrderByNumberOfWinsDescNumberOfTiesDescNumberOfLossesAsc();
}
