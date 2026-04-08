package cat.itacademy.blackjack.model;

import cat.itacademy.blackjack.exception.EmptyShoeException;
import cat.itacademy.blackjack.exception.InvalidGameActionException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "games")
public class Game {

    @Id
    private String id;

    private Long playerId;
    private List<Card> shoe;
    private Hand playerHand;
    private Hand dealerHand;
    private GameState state;
    private GameResult result;

    public Game() {
    }

    public Game(Long playerId, List<Card> shoe, Hand playerHand, Hand dealerHand, GameState state, GameResult result) {
        this.playerId = playerId;
        this.shoe = shoe;
        this.playerHand = playerHand;
        this.dealerHand = dealerHand;
        this.state = state;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public List<Card> getShoe() {
        return List.copyOf(shoe);
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public GameState getState() {
        return state;
    }

    public GameResult getResult() {
        return result;
    }

    public void hit() {
        assertGameNotFinished(GameAction.HIT);

        assertShoeHasCards();
        playerHand.pickCard(shoe);

        if (playerHand.getValue() > 21) {
            gameOver();
        }
    }

    public void stand() {
        assertGameNotFinished(GameAction.STAND);

        dealerPlay();
    }

    private void dealerPlay() {
        if (this.state == GameState.FINISHED) {
            throw new IllegalStateException("Internal error: dealerPlay was called but the game is already finished.");
        }

        while (dealerHand.getValue() < 17) {
            assertShoeHasCards();
            dealerHand.pickCard(shoe);
        }

        gameOver();
    }

    private void gameOver() {
        int playerHandValue = playerHand.getValue();
        int dealerHandValue = dealerHand.getValue();

        if (playerHandValue > 21) {
            result = GameResult.PLAYER_LOSES;
        } else if (dealerHandValue > 21) {
            result = GameResult.PLAYER_WINS;
        } else if (playerHandValue > dealerHandValue) {
            result = GameResult.PLAYER_WINS;
        } else if (dealerHandValue > playerHandValue) {
            result = GameResult.PLAYER_LOSES;
        } else {
            result = GameResult.TIE;
        }

        state = GameState.FINISHED;
    }

    private void assertGameNotFinished(GameAction action) {
        if (this.state == GameState.FINISHED) {
            throw new InvalidGameActionException(action, "The current game is already finished");
        }
    }

    private void assertShoeHasCards() {
        if (shoe == null || shoe.isEmpty()) {
            throw new EmptyShoeException();
        }
    }

    public static Game newGame(Long playerId) {
        List<Card> shoe = Card.newShuffledDeck();
        Hand playerHand = Hand.newHand(shoe);
        Hand dealerHand = Hand.newHand(shoe);

        return new Game(playerId, shoe, playerHand, dealerHand, GameState.IN_PROGRESS, GameResult.UNDETERMINED);
    }
}
