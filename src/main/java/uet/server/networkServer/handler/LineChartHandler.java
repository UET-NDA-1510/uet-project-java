package uet.server.networkServer.handler;

import uet.common.model.Auction.BidDTO;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.auctionDAO.BidtransactionDAO;
import uet.server.networkServer.RequestHandler;

import java.util.ArrayList;

public class LineChartHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        long auctionid = (long) request.getData();
        BidtransactionDAO bidtransactionDAO = new BidtransactionDAO();
        ArrayList<BidDTO> historyBid = bidtransactionDAO.getHistoryByAuctionId(auctionid);
        return new Response(Action.Line_Chart,"",historyBid,true);
    }
}
