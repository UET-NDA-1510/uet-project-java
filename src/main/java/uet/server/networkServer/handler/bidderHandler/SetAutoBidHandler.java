package uet.server.networkServer.handler.bidderHandler;

import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.auctionDAO.AutoBidDAO;
import uet.server.networkServer.RequestHandler;

import java.math.BigDecimal;

public class SetAutoBidHandler implements RequestHandler {
    @Override
    public Response handle(Request request) {
        AutoBidDAO autoBidDAO = new AutoBidDAO();
        String[] data = (String[]) request.getData();
        long auctionId = Long.parseLong(data[0]);
        long bidderId = Long.parseLong(data[1]);
        BigDecimal maxLimit = new BigDecimal(data[2]);
        BigDecimal stepPrice = new BigDecimal(data[3]);
        autoBidDAO.saveOrEnableAutoBid(auctionId, bidderId, maxLimit, stepPrice);
        System.err.println("tạo auto bid");
        return new Response(Action.AUTO_BID,"tt",null,true);
    }
}
