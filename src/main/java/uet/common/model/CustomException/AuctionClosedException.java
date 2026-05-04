package uet.common.model.CustomException;

public class AuctionClosedException extends RuntimeException {
    public AuctionClosedException(){
        super("Auction was end");
    }
    public AuctionClosedException(String mess){
        super(mess);
    }
}
