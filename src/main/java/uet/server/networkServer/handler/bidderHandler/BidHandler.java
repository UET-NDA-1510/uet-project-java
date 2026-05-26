package uet.server.networkServer.handler.bidderHandler;

import uet.common.model.Auction.Auction;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.DBConnection;
import uet.server.DAO.auctionDAO.AuctionDAO;
import uet.server.ServerMain;
import uet.server.networkServer.AuctionScheduler;
import uet.server.networkServer.RequestHandler;
import uet.server.service.auctionService.AuctionManager;
import uet.server.service.auctionService.BidService;
import uet.server.service.strategy.AutoBiddingStrategy;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class BidHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        BidService bidService = BidService.getInstance();
        AuctionDAO auctionDAO = new AuctionDAO();
        try (Connection connection = DBConnection.getConnection()){
            String[] data = (String[]) request.getData();
            // ép về đúng kiểu để dùng hàm place bid
            long auctionId = Long.parseLong(data[0]);
            long bidderId = Long.parseLong(data[1]);
            BigDecimal bid = new BigDecimal(data[2]);
            Auction auction = auctionDAO.findById(connection,auctionId);
            // đặt giá thủ công
            bidService.placeBid(auctionId,bidderId,bid);
            // cập nhật UI khi có lượt đặt giá mới
            Response uiUpdateRes = new Response(Action.NEW_BID_UPDATE, "Giá mới",bid, true);
            ServerMain.broadcast(uiUpdateRes);
            // lấy list những người đã từng đấu giá phiên này để gửi thông báo
            AuctionManager.getInstance().addParticipant(auctionId,bidderId);
            Set<Long> targetUsersSet = AuctionManager.getInstance().getParticipants(auctionId);
            ArrayList<Long> targetUserID = new ArrayList<>(targetUsersSet);
            targetUserID.remove(bidderId);
            // gửi pop up cho những ai đã đặt giá
            String mess = "Bidder có ID : "+bidderId+" ,đã đặt giá thành công cho phiên có ID : "+auctionId;
            if (!targetUserID.isEmpty()) {
                Response updateBid = new Response(Action.GET_NOTIFI_BID,mess,null,true);
                ServerMain.broadcastToTargetUsers(targetUserID,updateBid);
            }
            // anti sniping
            long secondsLeft = ChronoUnit.SECONDS.between(LocalDateTime.now(),auction.getEndTime());
            // Nếu thời gian còn lại <= 60 giây (1 phút)
            if (secondsLeft <= 60) {
                // Cộng thêm 60 giây nữa
                AuctionScheduler.getInstance().extendAuction(auction);
                // GỬI SÓNG PHÁT THANH CHO MỌI NGƯỜI ĐỂ CẬP NHẬT ĐỒNG HỒ ĐẾM NGƯỢC
                String antiSnipingMess = "Phiên đấu giá có ID : "+auction.getAuctionId()+"đã gia hạn thêm 1 phút!";
                Response extendRes = new Response(Action.AUCTION_EXTENDED, antiSnipingMess,null, true);
                ServerMain.broadcast(extendRes);
            }
            // auto biding
            broadcastAutoBiding(auctionDAO,auctionId,bidderId,targetUserID);
            // gửi thông báo cho chính người đã đặt giá
            return new Response(Action.PLACE_BID,"bạn đã đặt giá thành công",null,true);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ database khi đặt giá");
            return new Response(Action.PLACE_BID, "Lỗi khi lấy dữ liệu từ database", null, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println(" lỗi đa luồng khi đặt giá");
            return null;
        } catch (RuntimeException e) {
            return new Response(Action.PLACE_BID, e.getMessage(), null, false);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private void broadcastAutoBiding(AuctionDAO auctionDAO , long auctionId, long bidderId,ArrayList<Long> targetUserID){
        AuctionScheduler.getInstance().scheduleTask(() -> {
            try (Connection botConn = DBConnection.getConnection()) {
                // Lấy lại giá cao nhất mới nhất từ DB vì trong 1 giây qua có thể đã có người xen ngang
                Auction currentAuction = auctionDAO.findById(botConn, auctionId);
                // Thả dàn autoBid
                AutoBiddingStrategy auto = new AutoBiddingStrategy();
                auto.executeBidding(auctionId, bidderId, currentAuction.getCurrentHighestBid());
                // Kiểm tra kết quả
                Auction finalAuction = auctionDAO.findById(botConn, auctionId);
                // NẾU autobid THỰC SỰ CÓ ĐÈ GIÁ (Giá hiện tại > Giá 1 giây trước)
                if (finalAuction.getCurrentHighestBid().compareTo(currentAuction.getCurrentHighestBid()) > 0) {
                    // Sóng 1: Cập nhật lại LineChart bằng giá của autoBid
                    Response botUiUpdate = new Response(Action.NEW_BID_UPDATE, "Giá mới", finalAuction.getCurrentHighestBid(), true);
                    ServerMain.broadcast(botUiUpdate);
                    // Sóng 2: Gửi popup thông báo
                    if (!targetUserID.isEmpty()) {
                        String mess = "Bidder có ID : "+finalAuction.getHighestBidderId()+",đã đặt giá thành công cho phiên có ID : "+auctionId;
                        Response botNoti = new Response(Action.GET_NOTIFI_BID, mess, null, true);
                        targetUserID.add(bidderId);
                        ServerMain.broadcastToTargetUsers(targetUserID, botNoti);
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi chạy AutoBidding ngầm: " + e.getMessage());
            }
        }, 1, TimeUnit.SECONDS);
    }
}
