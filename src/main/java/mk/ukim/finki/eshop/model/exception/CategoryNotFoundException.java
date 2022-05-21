package mk.ukim.finki.eshop.model.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(){
        super("CategoryNotFoundException");
    }
}
