package uet.server.networkServer.handler;

import uet.common.model.items.Item;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.itemService.ItemService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetFuLLProductHandle implements RequestHandler {
    @Override
    public Response handle(Request request){
        ItemService itemService = ItemService.getInstance();
        long sellerID = (long) request.getData();
        try {
            ArrayList<Item> items = (ArrayList<Item>) itemService.findAllBySellerId(sellerID);
            return new Response(Action.GET_ALL_ITEMS,"Lay danh sach thanh cong",items,true);
        } catch (SQLException e) {
            System.err.println("lỗi khi lấy danh sách sản phẩm từ database");
            return new Response(Action.GET_ALL_ITEMS,"lỗi khi lấy danh sách",null,false);
        }
    }
}
