package uet.server.networkServer.handler;

import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.auctionDAO.AuctionDAO;
import uet.server.networkServer.RequestHandler;

import java.sql.SQLException;

public class getAuctionInforHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        AuctionDAO auctionDAO = new AuctionDAO();
        long auctionId = (long) request.getData();
        try {
            String[] arr = auctionDAO.getAuctionInformation(auctionId);
            return new Response(Action.GET_INFO_AUCTION_BY_ID,"lay thanh cong",arr,true);
        } catch (SQLException e) {
            System.err.println("lỗi khi lấy thông tin 1 bảng từ database");
            return new Response(Action.GET_INFO_AUCTION_BY_ID,"lay that bai",null,false);
        }
    }
}
