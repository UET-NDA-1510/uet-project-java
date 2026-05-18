package uet.server.networkServer.handler.sellerHandler;

import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.itemService.ItemService;

import java.sql.SQLException;

public class DeleteItemHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        long itemID = (Long) request.getData();
        try {
            ItemService itemService = ItemService.getInstance();
            itemService.deleteItem(itemID);
            return new Response(Action.DELETE_ITEM,"Xoá thành công",null,true);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
