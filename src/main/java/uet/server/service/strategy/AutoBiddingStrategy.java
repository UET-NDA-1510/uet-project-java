package uet.server.service.strategy;

import uet.common.model.Auction.AutoBidConfig;
import uet.common.model.CustomException.InvalidBidException;
import uet.server.DAO.auctionDAO.AutoBidDAO;
import uet.server.service.auctionService.BidService;

import java.math.BigDecimal;
import java.util.List;

public class AutoBiddingStrategy implements BiddingStrategy {

    private final AutoBidDAO autoBidDAO = new AutoBidDAO();
    @Override
    public boolean executeBidding(long auctionId, long bidderId, BigDecimal currentPrice) throws Exception {
        List<AutoBidConfig> activeBots = autoBidDAO.getAllActiveAutoBids(auctionId);
        BigDecimal currentHighestPrice = currentPrice;
        long currentLeaderId = bidderId;

        while (!activeBots.isEmpty()) {
            if (activeBots.size() == 1) {
                processSingleBot(auctionId, activeBots.get(0), currentLeaderId, currentHighestPrice);
                break;
            }

            AutoBidConfig topBot = activeBots.get(0);
            if (topBot.getBidderId() == currentLeaderId) {
                break;
            }

            AutoBidConfig secondBot = activeBots.get(1);
            BigDecimal targetPrice = computeTargetPrice(topBot, secondBot, currentHighestPrice);

            boolean canOutbid = targetPrice.compareTo(currentHighestPrice) > 0
                    && targetPrice.compareTo(secondBot.getMaxLimitPrice()) > 0;

            if (canOutbid) {
                boolean placed = tryPlaceBid(auctionId, topBot.getBidderId(), targetPrice);
                if (placed) {
                    currentHighestPrice = targetPrice;
                    currentLeaderId = topBot.getBidderId();
                    for (int i = 1; i < activeBots.size(); i++) {
                        disableBot(auctionId, activeBots.get(i).getBidderId());
                    }
                    break;
                }
            } else {
                disableBot(auctionId, topBot.getBidderId());
            }

            // Loại top bot khỏi danh sách in-memory, tiếp tục với người kế tiếp
            activeBots.remove(0);
        }

        return true;
    }
    // hàm xử lý nếu chỉ có 1 auto bid
    private void processSingleBot(long auctionId, AutoBidConfig bot, long currentLeaderId, BigDecimal currentHighestPrice) throws Exception {
        // nếu đứng đầu thì không phải đặt giá nữa
        if (bot.getBidderId() == currentLeaderId) {
            return;
        }
        // tính toán giá kế tiếp
        BigDecimal nextPrice = currentHighestPrice.add(bot.getStepPrice());
        /*/ kiểm tra xem có vượt qua mức giới hạn mà người dùng đặt không
            nếu quá thì lấy luôn mức giới hạn
         */
        if (nextPrice.compareTo(bot.getMaxLimitPrice()) > 0) {
            nextPrice = bot.getMaxLimitPrice();
        }
        // kiểm tra xem sau khi cập nhật có thể đặt giálớn hơn giá hiện tại không , nếu không thì tắt auto bid
        if (nextPrice.compareTo(currentHighestPrice) > 0) {
            tryPlaceBid(auctionId, bot.getBidderId(), nextPrice);
        } else {
            disableBot(auctionId, bot.getBidderId());
        }
    }
    // tính top bot cần đặt giá bao nhiêu để vượt được bot đứng thứ hai
    private BigDecimal computeTargetPrice(AutoBidConfig topBot, AutoBidConfig secondBot,
                                          BigDecimal currentHighestPrice) {
        BigDecimal base;
        if (currentHighestPrice.compareTo(secondBot.getMaxLimitPrice()) >= 0) {
            base = currentHighestPrice;
        } else {
            base = secondBot.getMaxLimitPrice();
        }

        BigDecimal target = base.add(topBot.getStepPrice());
        if (target.compareTo(topBot.getMaxLimitPrice()) > 0) {
            return topBot.getMaxLimitPrice();
        }
        return target;
    }
    // thử đặt giá , nếu fail thì ném ra exception
    private boolean tryPlaceBid(long auctionId, long bidderId, BigDecimal price) throws Exception{
        try {
            BidService.getInstance().placeBid(auctionId, bidderId, price);
            return true;
        } catch (InvalidBidException e) {
            disableBot(auctionId, bidderId);
            return false;
        }
    }
    // hàm để tắt auto bid
    private void disableBot(long auctionId, long bidderId) {
        autoBidDAO.disableAutoBidconfig(auctionId, bidderId);
    }
}