package uet.server.networkServer.handler.bidderHandler;

import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.DBConnection;
import uet.server.DAO.auctionDAO.AuctionDAO;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.networkServer.RequestHandler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class getAuctionInforHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        AuctionDAO auctionDAO = new AuctionDAO();
        String[] arr = (String[]) request.getData();
        long auctionId = Long.parseLong(arr[0]);
        long bidderID = Long.parseLong(arr[1]);
        try (Connection connection = DBConnection.getConnection()){
            BidderDAO bidderDAO = new BidderDAO();
            BigDecimal balance = bidderDAO.findById(connection,bidderID).getBalance();
            String[] data = auctionDAO.getAuctionInformation(auctionId);
            String[] newData = Arrays.copyOf(data, data.length + 1);
            newData[data.length] = balance.toString();
            return new Response(Action.GET_INFO_AUCTION_BY_ID,"lay thanh cong",newData,true);
        } catch (SQLException e) {
            System.err.println("lỗi khi lấy thông tin 1 bảng từ database");
            return new Response(Action.GET_INFO_AUCTION_BY_ID,"lay that bai",null,false);
        }
    }
}
