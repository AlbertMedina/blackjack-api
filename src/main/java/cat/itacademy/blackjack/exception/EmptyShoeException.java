package cat.itacademy.blackjack.exception;

public class EmptyShoeException extends RuntimeException {
    public EmptyShoeException() {
        super("The shoe is empty. No more cards can be drawn.");
    }
}
