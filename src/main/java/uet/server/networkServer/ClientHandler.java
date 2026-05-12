package uet.server.networkServer;

import uet.common.model.User.User;
import uet.common.payLoad.Action;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;
import uet.server.ServerMain;
import uet.server.networkServer.handler.*;

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
    private Long loggedInUsername = null;
    // nơi để đăng ký các hàm trả về response , khi có resquest
    private static final Map<Action,RequestHandler> handlerRegistry = new ConcurrentHashMap<>();
    static {
        handlerRegistry.put(Action.LOGIN,new LoginHandler());
        handlerRegistry.put(Action.REGISTER,new RegisterHandler());
        handlerRegistry.put(Action.CREATE_ITEM,new CreateProductHandler());
        handlerRegistry.put(Action.EDIT_ITEM,new EditProductHandler());
        handlerRegistry.put(Action.GET_ALL_ITEMS,new GetFuLLProductHandle());
        handlerRegistry.put(Action.CREATE_AUCTION,new CreateAuctionHandler());
        handlerRegistry.put(Action.GET_ALL_AUCTIONS,new GetALLauctionhandler());
        handlerRegistry.put(Action.GET_INFO_AUCTION_BY_ID,new getAuctionInforHandler());
        handlerRegistry.put(Action.PLACE_BID,new BidHandler());
        handlerRegistry.put(Action.GET_ITEM_PENDING,new GetFuLLProductPedingHandle());
        handlerRegistry.put(Action.GET_ALL_USER,new GetALLuserHandler());
        handlerRegistry.put(Action.DELETE_USER,new DeleteUserHandler());
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
                            if (request.getAction() == Action.LOGIN && response.isSuccess()) {
                                Object responseData = response.getData();
                                if (responseData instanceof User) {
                                    Long loggedInId = ((User) responseData).getId();
                                    this.setLoggedInUsername(loggedInId);
                                    System.out.println("Client đã đăng nhập với ID: " + this.loggedInUsername);
                                } else {
                                    System.err.println("Cảnh báo: Dữ liệu trả về từ Login không phải là đối tượng User.");
                                }
                            }
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
            ServerMain.onlineClients.remove(this);
            closeConnections();
        }
    }
    public void sendResponse(Response response) throws IOException{
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
    public void setLoggedInUsername(Long username) {
        this.loggedInUsername = username;
    }

    public Long getLoggedInUsername() {
        return loggedInUsername;
    }
}
