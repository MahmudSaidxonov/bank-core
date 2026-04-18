package uz.banking.bank_core.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException (String message) {
        super(message);
    }
}
