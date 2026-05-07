package uet.server.networkServer.handler;

import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.itemService.ItemService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;

public class CreateProductHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        String[] arr = (String[]) request.getData();
        ItemService itemService = ItemService.getInstance();
        long sellerID = Long.parseLong(arr[0]);
        String type = arr[1];
        String name = arr[2];
        BigDecimal price = new BigDecimal(arr[3]);
        String description = arr[4];
        String imageUrl = arr[5];
        String[] extraInfo = Arrays.copyOfRange(arr, 6, arr.length);
        try {
            itemService.createItem(sellerID,type,name,price,description,imageUrl,extraInfo);
            return new Response(Action.CREATE_ITEM,"tạo thành công",null,true);
        } catch (SQLException e) {
            System.err.println("lỗi khi tạo sản phẩm");
            return new Response(Action.CREATE_ITEM,"lỗi khi tạo sản phẩm",null,false);
        }
    }
}
