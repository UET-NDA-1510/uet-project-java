package uet.server.networkServer.handler.adminHandler;

import uet.common.model.CustomException.DataAccessException;
import uet.common.model.User.User;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.UserService;

import java.util.ArrayList;

public class GetALLuserHandler implements RequestHandler {
    @Override
    public Response handle(Request request){
        try {
            UserService userService = UserService.getInstance();
            ArrayList<User> users = (ArrayList<User>) userService.loadUsersFromDatabase();
            return new Response(Action.GET_ALL_USER,"thanh cong",users,true);
        } catch (DataAccessException e){
            return new Response(Action.GET_ALL_USER,e.getMessage(),null,false);
        }
    }
}
