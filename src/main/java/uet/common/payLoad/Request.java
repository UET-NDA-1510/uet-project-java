package uet.common.payLoad;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private Action action;
    private Object data;
    public Request(Action action,Object data){
        this.action = action;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public Action getAction() {
        return action;
    }
}
