package uet.DAO;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
public class DBConnection {
    private static HikariDataSource ds;
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/auctiondb");
            config.setUsername("root");
            config.setPassword("123456");
            config.setMaximumPoolSize(5);  // Số lượng connection tối đa trong pool
            config.setConnectionTimeout(30000);   // thời gian chờ tối đa 30s
            config.setMaxLifetime(1800000);
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    // Hàm gọi khi tắt Server để giải phóng tài nguyên
    public static void closePool() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }
}