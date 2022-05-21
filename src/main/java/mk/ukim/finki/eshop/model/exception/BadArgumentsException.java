package mk.ukim.finki.eshop.model.exception;

public class BadArgumentsException extends RuntimeException {
    public BadArgumentsException(){
        super("Invalid arguments");
    }
}
