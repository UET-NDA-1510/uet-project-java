package uet.client.networkClient;

public class ControllerManager {
    private SocketClient client;
    private ResponseObserver current;
    private ControllerManager() {}
    private static class Holder {
        private static final ControllerManager instance = new ControllerManager();
    }
    public static ControllerManager getInstance() {
        return Holder.instance;
    }
    public void init(SocketClient client){
        this.client = client;
    }
    public void setCurrent(Object  controller){
        if (current != null){
            client.removeObservers(this.current);
        }
        if (controller instanceof ResponseObserver observer) {
            this.current = observer;
            client.addObserver(observer);
        } else {
            current = null;
        }
    }
}
