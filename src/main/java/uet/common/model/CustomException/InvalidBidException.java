package uet.common.model.CustomException;

public class InvalidBidException extends RuntimeException {
    public InvalidBidException() {
        super("Invalid bid");
    }
    public InvalidBidException(String message) {
        super(message);
    }
}
