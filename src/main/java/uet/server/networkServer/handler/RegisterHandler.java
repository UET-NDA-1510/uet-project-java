package uet.server.networkServer.handler;

import uet.common.model.CustomException.DataAccessException;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.RequestHandler;
import uet.server.service.authService.AuthService;

import java.time.DateTimeException;
import java.time.LocalDate;

public class RegisterHandler implements RequestHandler {
    @Override
    public Response handle(Request request) {
        AuthService authService = AuthService.getInstance();
        String[] arr = (String[]) request.getData();
        String name = arr[0];
        String email = arr[1];
        String password = arr[2];
        String role = arr[3];
        LocalDate dateOfBirth = LocalDate.parse(arr[4]);
        try {
            authService.register(name,email,password,role,dateOfBirth);
            Response response = new Response(Action.REGISTER,"Đăng ký thành công",null,true);
            return response;
        } catch (DataAccessException e){
            Response response = new Response(Action.REGISTER,e.getMessage(),null,false);
            return response;
        }
    }
}
