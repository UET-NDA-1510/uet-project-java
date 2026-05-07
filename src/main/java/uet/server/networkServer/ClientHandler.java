package uet.server.networkServer;

import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.networkServer.handler.LoginHandler;
import uet.server.networkServer.handler.RegisterHandler;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable{
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    // nơi để đăng ký các hàm trả về response , khi có resquest
    private static final Map<Action,RequestHandler> handlerRegistry = new ConcurrentHashMap<>();
    static {
        handlerRegistry.put(Action.LOGIN,new LoginHandler());
        handlerRegistry.put(Action.REGISTER,new RegisterHandler());
    }
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            // flush ObjectOutputStream ngay sau khi khởi tạo để tránh Deadlock với Client
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                Object object = in.readObject();
                if (object instanceof Request) {
                    Request request = (Request) object;
                    RequestHandler handler = handlerRegistry.get(request.getAction());
                    if (handler != null) {
                        try {
                            Response response = handler.handle(request);
                            sendResponse(response);
                        } catch (Exception ex) {
                            System.err.println("Lỗi logic khi xử lý request: " + ex.getMessage());
                            sendResponse(new Response(Action.ERROR, "Lỗi nội bộ Server: " + ex.getMessage(), null, false));
                        }
                    } else {
                        sendResponse(new Response(Action.ERROR, "Hành động chưa được hỗ trợ!", null, false));
                    }
                }
            }
        } catch (EOFException e1) {
            System.out.println("Client đã chủ động ngắt kết nối.");
        } catch (IOException e) {
            System.err.println("Có lỗi I/O hoặc Client ngắt kết nối đột ngột: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Lớp đối tượng không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi xử lý Client không xác định: " + e.getMessage());
        } finally {
            closeConnections();
        }
    }
    private void sendResponse(Response response) throws IOException{
        if (out != null){
            out.reset();
            out.writeObject(response);
            out.flush();
        }
    }
    private void closeConnections() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Lỗi đóng kết nối: " + e.getMessage());
        }
    }
}
