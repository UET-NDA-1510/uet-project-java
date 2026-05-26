package uet.server.networkServer.handler.bidderHandler;

import uet.common.model.items.Item;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.itemService.ItemService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Base64;

public class ProductInforHandler implements RequestHandler {
    @Override
    public Response handle(Request request) {
        long itemId = (long) request.getData();
        try {
            Item item = ItemService.getInstance().findById(itemId);
            String name = item.getName();
            String description = item.getDescription();
            String imagepath = item.getImageUrl();
            String base64ToReturn = "";
            Path path = Paths.get(imagepath);
            byte[] imageBytes = Files.readAllBytes(path);
            base64ToReturn = Base64.getEncoder().encodeToString(imageBytes);
            String[] data = {name,description,base64ToReturn};
            return new Response(Action.GET_ITEM_ByID,"Lấy thành công",data,true);
        } catch (SQLException e){
            e.printStackTrace();
            return new Response(Action.GET_ITEM_ByID,"lỗi dữ liệu từ server",null,false);
        } catch (IOException e){
            e.getMessage();
            return new Response(Action.GET_ITEM_ByID,"lỗi dữ liệu từ server",null,false);
        }
    }
}
