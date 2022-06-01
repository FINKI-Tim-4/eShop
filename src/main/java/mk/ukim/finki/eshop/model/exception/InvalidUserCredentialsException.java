package mk.ukim.finki.eshop.model.exception;



public class InvalidUserCredentialsException extends RuntimeException {
    public InvalidUserCredentialsException(){
        super("Invalid User");
    }
}
