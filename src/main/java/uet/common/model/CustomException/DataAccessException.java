package uet.common.model.CustomException;

public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket

    public DataAccessException(String message) {
        super(message);
    }
}
