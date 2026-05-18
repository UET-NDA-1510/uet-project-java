package uet.server.networkServer.handler.adminHandler;

import uet.common.model.CustomException.DataAccessException;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.UserService;

public class DeleteUserHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        String[] arr = (String[]) request.getData();
        long id = Long.parseLong(arr[0]);
        String role = arr[1];
        try {
            UserService userService = UserService.getInstance();
            userService.deleteUserFromDB(id,role);
            return new Response(Action.DELETE_USER,"Xoá thành công",null,true);
        } catch (DataAccessException e){
            e.printStackTrace();
            return new Response(Action.DELETE_USER,e.getMessage(),null,false);
        }
    }
}
