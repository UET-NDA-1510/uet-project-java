package uet.server.networkServer;

import uet.common.model.Auction.Auction;
import uet.common.model.User.User;
import uet.common.model.items.ItemStatus;
import uet.common.payLoad.Action;
import uet.common.payLoad.Response;
import uet.server.DAO.DBConnection;
import uet.server.DAO.auctionDAO.AuctionDAO;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.ServerMain;
import uet.server.service.auctionService.AuctionService;
import uet.server.service.itemService.ItemService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.*;

public class AuctionScheduler {
    AuctionService auctionService = AuctionService.getInstance();
    private static final AuctionScheduler instance = new AuctionScheduler();
    // lưu trữ các thời gian đóng cửa của phiên đấu giá
    private final Map<Long, ScheduledFuture<?>> endTasks = new ConcurrentHashMap<>();
    // luồng chuyên để chạy ngầm đếm ngược thời gian
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
        long delayToEnd = ChronoUnit.SECONDS.between(now, auction.getEndTime());

        if (delayToEnd <= 0) {
            // Đã quá giờ kết thúc -> Ép đóng cửa luôn nếu chưa đóng
            if (auction.getState() != Auction.AuctionState.FINISHED) {
                endAuction(auction);
            }
            // Đã xong xuôi (hoặc đã quá hạn) thì thoát hàm luôn (return).
            // Không cần quan tâm đến giờ bắt đầu nữa.
            return;
        } else {
            // Chưa tới giờ kết thúc -> Lên lịch báo thức ĐÓNG CỬA
            ScheduledFuture<?> endTask = scheduler.schedule(() -> endAuction(auction), delayToEnd, TimeUnit.SECONDS);
            endTasks.put(auction.getAuctionId(), endTask);
        }
        long delayToStart = ChronoUnit.SECONDS.between(now, auction.getStartTime());
        if (delayToStart <= 0) {
            // Đã qua giờ bắt đầu (nhưng chưa hết giờ) -> Ép mở cửa luôn nếu đang ở trạng thái OPEN/PENDING
            if (auction.getState() == Auction.AuctionState.OPEN) {
                startAuction(auction);
            }
        } else {
            // Chưa tới giờ bắt đầu -> Lên lịch báo thức MỞ CỬA
            scheduler.schedule(() -> startAuction(auction), delayToStart, TimeUnit.SECONDS);
        }
    }
    private void startAuction(Auction auction) {
        try {
            auctionService.startAuction(auction.getAuctionId());
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("lỗi khi tự động bắt đầu phiên đấu giá");
        }
        // 2. Bắn thông báo Realtime cho TẤT CẢ các máy Client đang online
        Response res = new Response(Action.AUCTION_STARTED, "Phiên đấu giá có Id " +auction.getAuctionId()+" đã chính thức bắt đầu.", auction, true);
        ServerMain.broadcast(res);
    }

    private void endAuction(Auction auction) {
        try {
            Connection connection = DBConnection.getConnection();
            BidderDAO bidderDAO = new BidderDAO();
            auctionService.finishAuction(auction.getAuctionId());
            User user = bidderDAO.findById(connection, auction.getHighestBidderId());
            ItemService.getInstance().updateItemStatus(auction.getItemId(), ItemStatus.SOLD.name());
            endTasks.remove(auction.getAuctionId());
            // 3. Bắn thông báo Realtime cho TẤT CẢ các máy
            Response res = new Response(Action.AUCTION_ENDED,"Phiên đấu giá có Id " +auction.getAuctionId()+" đã kết thúc.Người win là "+user.getUsername(), auction, true);
            ServerMain.broadcast(res);
            auctionService.markAuctionPaid(auction.getAuctionId());
        } catch (SQLException e){
            System.err.println("lỗi khi lấy bidder về từ database khi hoàn thành phiên");
        }catch (Exception e) {
            e.printStackTrace();
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
    // anti -sniping
    public void extendAuction(Auction auction){
        // 1. Hủy lịch đóng cửa cũ
        ScheduledFuture<?> oldTask = endTasks.get(auction.getAuctionId());
        if (oldTask != null && !oldTask.isDone()) {
            oldTask.cancel(false); // Hủy lệnh báo thức cũ
        }
        // 2. Đặt lịch đóng cửa mới
        LocalDateTime newEndTime = auction.getEndTime().plusSeconds(60);
        auction.setEndTime(newEndTime);
        long newDelayToEnd = ChronoUnit.SECONDS.between(LocalDateTime.now(), newEndTime);
        AuctionDAO auctionDAO = new AuctionDAO();
        auctionDAO.updateEndTime(auction.getAuctionId(),newEndTime);
        ScheduledFuture<?> newTask = scheduler.schedule(() -> endAuction(auction), newDelayToEnd, TimeUnit.SECONDS);
        endTasks.put(auction.getAuctionId(), newTask);
    }
    // auto bidding
    // Trong class AuctionScheduler.java
    public void scheduleTask(Runnable task, long delay, TimeUnit unit) {
        scheduler.schedule(task, delay, unit);
    }
}
