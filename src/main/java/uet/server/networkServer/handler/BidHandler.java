package uet.server.networkServer.handler;

import uet.common.model.CustomException.AuctionClosedException;
import uet.common.model.CustomException.DataAccessException;
import uet.common.model.CustomException.InvalidBidException;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.ServerMain;
import uet.server.networkServer.RequestHandler;
import uet.server.service.auctionService.AuctionManager;
import uet.server.service.auctionService.BidService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;


public class BidHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        BidService bidService = BidService.getInstance();
        try {
            String[] data = (String[]) request.getData();
            // ép về đúng kiểu để dùng hàm place bid
            long auctionId = Long.parseLong(data[0]);
            long bidderId = Long.parseLong(data[1]);
            BigDecimal bid = new BigDecimal(data[2]);
            // đặt giá
            bidService.placeBid(auctionId,bidderId,bid);
            // lấy list những người đã từng đấu giá phiên này để gửi thông báo
            AuctionManager.getInstance().addParticipant(auctionId,bidderId);
            Set<Long> targetUsersSet = AuctionManager.getInstance().getParticipants(auctionId);
            ArrayList<Long> targetUserID = new ArrayList<>(targetUsersSet);
            targetUserID.remove(bidderId);
            // cập nhật UI khi có lượt đặt giá mới
            Response uiUpdateRes = new Response(Action.NEW_BID_UPDATE, "Giá mới",null, true);
            ServerMain.broadcast(uiUpdateRes);
            // gửi pop up cho những ai đã đặt giá
            String mess = "Bidder có ID : "+bidderId+",đã đặt giá thành công cho phiên có ID : "+auctionId;
            if (!targetUserID.isEmpty()) { // Có người để gửi thì mới gửi
                Response updateBid = new Response(Action.GET_NOTIFI_BID,mess,null,true);
                ServerMain.broadcastToTargetUsers(targetUserID,updateBid);
            }
            // gửi thông báo cho chính người đã đặt giá
            return new Response(Action.PLACE_BID,"bạn đã đặt giá thành công",null,true);
        } catch (AuctionClosedException e){
            return new Response(Action.PLACE_BID,e.getMessage(),null,false);
        } catch (InvalidBidException e){
            return new Response(Action.PLACE_BID,e.getMessage(),null,false);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ database khi đặt giá");
            return new Response(Action.PLACE_BID, "Lỗi khi lấy dữ liệu từ database", null, false);
        } catch (DataAccessException e){
            return new Response(Action.PLACE_BID, e.getMessage(), null, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println(" lỗi đa luồng khi đặt giá");
            return null;
        } catch (RuntimeException e) {
            return new Response(Action.PLACE_BID, e.getMessage(), null, false);
        }
    }
}
