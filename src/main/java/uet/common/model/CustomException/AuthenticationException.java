package uet.common.model.CustomException;

public class AuthenticationException extends RuntimeException{
    private static final long serialVersionUID = 1L;   // id để gửi dữ liệu cho socket

    public AuthenticationException(){
        super("Data error");
    }
    public AuthenticationException(String mess){
        super(mess);
    }
}
