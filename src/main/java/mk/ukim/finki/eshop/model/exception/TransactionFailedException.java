package mk.ukim.finki.eshop.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String userId, String message) {
        super(String.format("Transaction for user %s failed! Message: %s", userId, message));
    }
}
