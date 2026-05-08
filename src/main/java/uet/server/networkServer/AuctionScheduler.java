package uet.server.networkServer;

import uet.common.model.Auction.Auction;
import uet.common.model.User.User;
import uet.common.payLoad.Action;
import uet.common.payLoad.Response;
import uet.server.DAO.DBConnection;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.ServerMain;
import uet.server.service.auctionService.AuctionService;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class AuctionScheduler {
    AuctionService auctionService = AuctionService.getInstance();
    private static final AuctionScheduler instance = new AuctionScheduler();
    // 1 luồng chuyên để chạy ngầm đếm ngược thời gian
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> {
        Thread t = new Thread(r, "auction-scheduler");
        t.setDaemon(true); // JVM không đợi thread này khi tắt
        return t;
    });

    private AuctionScheduler() {}
    public static AuctionScheduler getInstance() {
        return instance;
    }

    // Gọi hàm này ngay khi Seller vừa tạo xong 1 sản phẩm đấu giá mới
    public void scheduleAuctionEvents(Auction auction) {
        LocalDateTime now = LocalDateTime.now();
        // 1. Tính số giây từ bây giờ đến lúc BẮT ĐẦU
        long delayToStart = ChronoUnit.SECONDS.between(now, auction.getStartTime());
        if (delayToStart > 0) {
            scheduler.schedule(() -> startAuction(auction), delayToStart, TimeUnit.SECONDS);
        } else if (auction.getState() == Auction.AuctionState.OPEN) {
            // Nếu lúc nạp lên mà đã quá giờ start -> Mở luôn
            startAuction(auction);
        }

        // 2. Tính số giây từ bây giờ đến lúc KẾT THÚC
        long delayToEnd = ChronoUnit.SECONDS.between(now, auction.getEndTime());
        if (delayToEnd > 0) {
            scheduler.schedule(() -> endAuction(auction), delayToEnd, TimeUnit.SECONDS);
        }
    }

    private void startAuction(Auction auction) {
        try {
            auctionService.startAuction(auction.getAuctionId());
        } catch (Exception e){
            System.err.println("lỗi khi tự động bắt đầu phiên đấu giá");
        }
        // 2. Bắn thông báo Realtime cho TẤT CẢ các máy Client đang online
        Response res = new Response(Action.AUCTION_STARTED, "Phiên đấu giá có Id" +auction.getAuctionId()+"đã chính thức bắt đầu.", auction, true);
        ServerMain.broadcast(res);
    }

    private void endAuction(Auction auction) {
        try {
            Connection connection = DBConnection.getConnection();
            BidderDAO bidderDAO = new BidderDAO();
            auctionService.finishAuction(auction.getAuctionId());
            User user = bidderDAO.findById(connection, auction.getHighestBidderId());
            // 3. Bắn thông báo Realtime cho TẤT CẢ các máy
            Response res = new Response(Action.AUCTION_ENDED,"Phiên đấu giá có Id" +auction.getAuctionId()+"đã kết thúc.Người win là "+user.getUsername(), auction, true);
            ServerMain.broadcast(res);
        } catch (SQLException e){
            System.err.println("lỗi khi lấy bidder về từ database khi hoàn thành phiên");
        }catch (Exception e) {
            System.err.println("lỗi khi tự động kết thúc phiên đấu giá phiên đấu giá");
        }
    }
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
