package uet.server.networkServer.handler;

import uet.common.model.Auction.Auction;
import uet.common.model.items.ItemStatus;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.AuctionScheduler;
import uet.server.networkServer.RequestHandler;
import uet.server.service.auctionService.AuctionService;
import uet.server.service.itemService.ItemService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CreateAuctionHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        ItemService itemService = ItemService.getInstance();
        String[] arr = (String[]) request.getData();
        AuctionService auctionService = AuctionService.getInstance();
        long itemId = Long.parseLong(arr[0]);
        long sellerId = Long.parseLong(arr[1]);
        BigDecimal startingPrice = new BigDecimal(arr[2]);
        LocalDateTime start_time = LocalDateTime.parse(arr[3]);
        LocalDateTime end_time = LocalDateTime.parse(arr[4]);
        itemService.updateItemStatus(itemId,ItemStatus.IN_AUCTION.name());
        try {
            Auction newAuction = auctionService.createAuction(itemId,sellerId,startingPrice,start_time,end_time);
            AuctionScheduler.getInstance().scheduleAuctionEvents(newAuction);
            return new Response(Action.CREATE_AUCTION,"tao thành công",null,true);
        } catch (SQLException e) {
            System.err.println("lỗi khi tạo phiên đấu giá");
            System.out.println("SQL Start: " + java.sql.Timestamp.valueOf(start_time));
            System.out.println("SQL End:   " + java.sql.Timestamp.valueOf(end_time));
            e.printStackTrace();
            return new Response(Action.CREATE_AUCTION,"loi khi tạo",null,false);
        }
    }
}
