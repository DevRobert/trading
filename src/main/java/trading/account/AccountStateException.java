package trading.account;

/**
 * Exception is thrown when an action does not comply with the current account state.
 */
public class AccountStateException extends RuntimeException {
    public AccountStateException(String message) {
        super(message);
    }
}
