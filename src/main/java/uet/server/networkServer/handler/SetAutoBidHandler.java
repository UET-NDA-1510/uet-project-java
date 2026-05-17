package uet.server.networkServer.handler;

import uet.common.model.Auction.Auction;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.DBConnection;
import uet.server.DAO.auctionDAO.AutoBidDAO;
import uet.server.ServerMain;
import uet.server.networkServer.RequestHandler;
import uet.server.service.strategy.AutoBiddingStrategy;

import java.math.BigDecimal;
import java.sql.Connection;

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
        return null;
    }
}
