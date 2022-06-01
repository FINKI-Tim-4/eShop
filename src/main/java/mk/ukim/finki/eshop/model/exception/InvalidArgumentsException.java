package mk.ukim.finki.eshop.model.exception;


public class InvalidArgumentsException extends RuntimeException{
    public InvalidArgumentsException(){
        super("Invalid username or password.");
    }
}
