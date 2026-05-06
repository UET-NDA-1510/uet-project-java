package uet.test;

import org.junit.jupiter.api.Test;
import uet.common.model.Auction.Auction;
import uet.common.model.CustomException.InvalidBidException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {

    @Test
    void testDatGiaThanhCong() {
        // 1. Dữ liệu giả lập: Tạo 1 phiên đấu giá với giá khởi điểm 100$
        Auction auction = new Auction(
                1L, 
                100L, 
                new BigDecimal("100.00"), 
                LocalDateTime.now().minusHours(1), 
                LocalDateTime.now().plusHours(2)
        );
        
        // 2. Chuyển sang trạng thái đang chạy thì mới được đấu giá
        auction.start();

        // 3. Thực hiện hành động: Đặt giá 150$ từ người có ID = 99
        auction.updateHighestBid(new BigDecimal("150.00"), 99L);

        // 4. Kiểm tra kết quả (Assertions)
        assertEquals(new BigDecimal("150.00"), auction.getCurrentHighestBid(), "Giá cao nhất phải cập nhật thành 150");
        assertEquals(99L, auction.getHighestBidderId(), "ID người giữ giá phải là 99");
    }

    @Test
    void testDatGiaThapHonGiaHienTai_BaoLoi() {
        Auction auction = new Auction(
                1L, 
                100L, 
                new BigDecimal("100.00"), 
                LocalDateTime.now(), 
                LocalDateTime.now().plusHours(2)
        );
        auction.start();

        // Dùng assertThrows để bẫy lỗi: 
        // Kỳ vọng hệ thống sẽ quăng ra lỗi InvalidBidException khi đặt giá 80$ (nhỏ hơn 100$)
        Exception exception = assertThrows(InvalidBidException.class, () -> {
            auction.updateHighestBid(new BigDecimal("80.00"), 99L);
        });

        // Kiểm tra xem câu báo lỗi có đúng như mình thiết kế không
        assertTrue(exception.getMessage().contains("Phải đặt giá cao hơn"));
    }
}