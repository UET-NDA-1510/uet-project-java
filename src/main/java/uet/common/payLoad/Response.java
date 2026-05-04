package uet.common.payLoad;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private Action action;
    private String message;
    private Object data;
    public Response(Action action,String message,Object data,boolean success){
        this.action = action;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Action getAction() {
        return action;
    }
}
