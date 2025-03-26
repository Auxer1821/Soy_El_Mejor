package exceptions.altaUsuarios;

public class NotUserFoundException extends RuntimeException{
    public NotUserFoundException(String message) {
        super(message);
    }
}
