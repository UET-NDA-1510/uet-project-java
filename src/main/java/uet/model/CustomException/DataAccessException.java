package uet.model.CustomException;

public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }
}
