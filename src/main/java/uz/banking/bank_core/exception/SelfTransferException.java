package uz.banking.bank_core.exception;

public class SelfTransferException extends IllegalArgumentException {
    public SelfTransferException(String message) {
        super(message);
    }
}
