package mk.ukim.finki.eshop.model.exception;

public class PasswordDoNotMatchException extends RuntimeException{
    public PasswordDoNotMatchException(){
        super("Passwords do not match");
    }
}
