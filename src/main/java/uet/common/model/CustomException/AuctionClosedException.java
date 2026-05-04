package uet.common.model.CustomException;

public class AuctionClosedException extends RuntimeException {
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket
    public AuctionClosedException(){
        super("Auction was end");
    }
    public AuctionClosedException(String mess){
        super(mess);
    }
}
