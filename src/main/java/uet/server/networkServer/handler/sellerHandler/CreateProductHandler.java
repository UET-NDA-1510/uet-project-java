package uet.server.networkServer.handler.sellerHandler;

import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.itemService.ItemService;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;

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
            String[] parts =imageUrl.split("\\|\\|\\|");
            String extension = parts[0];   // Sẽ là ".jpg", ".png", v.v.
            String base64Image = parts[1]; // Dữ liệu ảnh thực tế
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String myFolderPath = "C:/Users/PC/Downloads";

            // 4. Tạo tên file mới CÓ ĐUÔI LINH HOẠT
            String newFileName = "item_" + System.currentTimeMillis() + extension;
            Path filePath = Paths.get(myFolderPath, newFileName);
            Files.write(filePath, imageBytes);
            String savedImagePath = filePath.toString();
            itemService.createItem(sellerID,type,name,price,description,savedImagePath,extraInfo);
            return new Response(Action.CREATE_ITEM,"tạo thành công",null,true);
        } catch (SQLException e) {
            System.err.println("lỗi khi tạo sản phẩm");
            return new Response(Action.CREATE_ITEM,"lỗi khi tạo sản phẩm",null,false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
