package uet.server.networkServer.handler;

import uet.common.model.items.Item;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.itemService.ItemService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;

public class EditProductHandler implements RequestHandler {
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
        long olditemId = Long.parseLong(arr[6]);
        String[] extraInfo = Arrays.copyOfRange(arr, 7, arr.length);
        try {
            boolean isUpdate = itemService.updateItem(sellerID, type, name, price, description, imageUrl, olditemId, extraInfo);
            if (isUpdate) {
                System.out.println("Đã cập nhật thành công Item ID: " + olditemId);
                return new Response(Action.EDIT_ITEM, "Sửa thành công", null, true);
            } else {
                // NẾU CHẠY VÀO ĐÂY: Câu lệnh SQL chạy thành công nhưng không có dòng nào được sửa!
                System.err.println("Không tìm thấy sản phẩm với ID: " + olditemId + " để sửa.");
                return new Response(Action.EDIT_ITEM, "Không tìm thấy sản phẩm", null, false);
            }
//            return new Response(Action.EDIT_ITEM,"sửa thành công",null,true);
        } catch (SQLException e) {
            System.err.println("lỗi khi sửa sản phẩm");
            return new Response(Action.EDIT_ITEM,"lỗi khi sửa sản phẩm",null,false);
        }
    }
}
