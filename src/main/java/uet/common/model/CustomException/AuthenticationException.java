package uet.common.model.CustomException;

public class AuthenticationException extends RuntimeException{
    public AuthenticationException(){
        super("Data error");
    }
    public AuthenticationException(String mess){
        super(mess);
    }
}
