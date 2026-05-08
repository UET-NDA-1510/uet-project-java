package uet.server.networkServer.handler;

import uet.common.model.Auction.Auction;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.auctionService.AuctionService;

import java.sql.SQLException;
import java.util.ArrayList;

public class GetALLauctionhandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        AuctionService auctionService = AuctionService.getInstance();
        ArrayList<Auction> items = (ArrayList<Auction>) auctionService.getALLAuction();
        return new Response(Action.GET_ALL_AUCTIONS,"Lay danh sach thanh cong",items,true);
    }
}
