package uet.common.model.CustomException;
public class AuctionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket
    public AuctionNotFoundException(String message) {
        super(message);
    }
}
