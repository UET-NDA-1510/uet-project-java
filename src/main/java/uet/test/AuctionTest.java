package uet.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uet.common.model.Auction.Auction;
import uet.common.model.CustomException.InvalidBidException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionTest {
    private Auction auction;

    @BeforeEach
    void setUp() {
        auction = new Auction(
            101L, 
            202L, 
            new BigDecimal("100.0"), 
            LocalDateTime.now().minusMinutes(5), 
            LocalDateTime.now().plusHours(1)
        );
    }

    @Test
    @DisplayName("Đặt giá thành công khi phiên đang RUNNING và giá cao hơn")
    void testDatGiaHopLe() {
        auction.start(); // Phải chuyển sang RUNNING mới đặt giá được
        
        BigDecimal bidAmount = new BigDecimal("150.0");
        long bidderId = 25020072L;

        assertDoesNotThrow(() -> auction.updateHighestBid(bidAmount, bidderId));
        assertEquals(bidAmount, auction.getCurrentHighestBid());
        assertEquals(bidderId, auction.getHighestBidderId());
    }

    @Test
    @DisplayName("Báo lỗi InvalidBidException khi đặt giá thấp hơn giá hiện tại")
    void testDatGiaThap() {
        auction.start();
        BigDecimal lowBid = new BigDecimal("90.0");

        // Kiểm tra xem có ném đúng InvalidBidException không
        assertThrows(InvalidBidException.class, () -> {
            auction.updateHighestBid(lowBid, 999L);
        });
    }

    @Test
    @DisplayName("Báo lỗi IllegalStateException khi đặt giá lúc phiên chưa bắt đầu (OPEN)")
    void testDatGiaKhiChuaBatDau() {
        // Trạng thái mặc định là OPEN, chưa gọi auction.start()
        BigDecimal bidAmount = new BigDecimal("200.0");

        assertThrows(IllegalStateException.class, () -> {
            auction.updateHighestBid(bidAmount, 999L);
        });
    }

    @Test
    @DisplayName("Báo lỗi IllegalStateException khi đặt giá lúc phiên đã kết thúc (FINISHED)")
    void testDatGiaKhiDaKetThuc() {
        auction.start();
        auction.finish(); // Chuyển trạng thái sang FINISHED
        
        BigDecimal bidAmount = new BigDecimal("200.0");

        assertThrows(IllegalStateException.class, () -> {
            auction.updateHighestBid(bidAmount, 999L);
        });
    }

    @Test
    @DisplayName("Kiểm tra logic thời gian kết thúc (isActive)")
    void testIsActive() {
        // isActive trả về true nếu hiện tại trước endTime
        assertTrue(auction.isActive());
        
        // Thử set endTime về quá khứ
        auction.setEndTime(LocalDateTime.now().minusMinutes(1));
        assertFalse(auction.isActive());
    }
}