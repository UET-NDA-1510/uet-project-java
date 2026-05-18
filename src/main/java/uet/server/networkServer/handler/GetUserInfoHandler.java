package uet.server.networkServer.handler;

import uet.common.model.User.User;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.DAO.DBConnection;
import uet.server.DAO.userDAO.AdminDAO;
import uet.server.DAO.userDAO.BidderDAO;
import uet.server.DAO.userDAO.SellerDAO;
import uet.server.networkServer.RequestHandler;

import java.sql.Connection;

public class GetUserInfoHandler implements RequestHandler {
    @Override
    public Response handle(Request request) {
        try {
            // Nhận [ID, Role] từ Client
            Object[] data = (Object[]) request.getData();
            long userId = (Long) data[0];
            String role = (String) data[1];

            User user = null;
            
            // Dùng đúng DAO để chọc xuống DB lấy dữ liệu mới nhất
            try (Connection connect = DBConnection.getConnection()) {
                if ("Bidder".equalsIgnoreCase(role)) {
                    user = new BidderDAO().findById(connect, userId);
                } else if ("Seller".equalsIgnoreCase(role)) {
                    user = new SellerDAO().findById(connect, userId);
                } else if ("Admin".equalsIgnoreCase(role)) {
                    user = new AdminDAO().findById(connect, userId);
                }
            }

            if (user != null) {
                return new Response(Action.GET_USER_INFO, "Thành công", user, true);
            } else {
                return new Response(Action.GET_USER_INFO, "Không tìm thấy user", null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Action.GET_USER_INFO, "Lỗi Server: " + e.getMessage(), null, false);
        }
    }
}