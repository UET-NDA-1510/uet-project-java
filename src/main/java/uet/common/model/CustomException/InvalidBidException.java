package uet.common.model.CustomException;

public class InvalidBidException extends RuntimeException {
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket

    public InvalidBidException() {
        super("Invalid bid");
    }
    public InvalidBidException(String message) {
        super(message);
    }
}
