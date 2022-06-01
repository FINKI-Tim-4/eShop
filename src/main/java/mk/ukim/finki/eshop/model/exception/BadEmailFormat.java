package mk.ukim.finki.eshop.model.exception;

public class BadEmailFormat extends RuntimeException{
    public BadEmailFormat(){
        super("Bad Email Format");
    }
}
