package uet.server.networkServer.handler;

import uet.common.model.CustomException.AuthenticationException;
import uet.common.model.User.User;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.authService.AuthService;

public class LoginHandler implements RequestHandler {
    @Override
    public Response handle(Request request) {
        AuthService authService = AuthService.getInstance();
        String text = (String) request.getData();
        String[] arr = text.split(" ");
        String username = arr[0];
        String password = arr[1];
        String role = arr[2];
        try {
            User user = authService.login(username,password,role);
            Response response = new Response(Action.LOGIN,"đăng nhập thành công",user,true);
            return response;
        } catch (AuthenticationException e){
            Response response = new Response(Action.LOGIN,e.getMessage(),null,false);
            return response;
        }
    }
}
