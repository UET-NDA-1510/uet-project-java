package uet.client.networkClient;

import javafx.application.Platform;
import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketClient {
    private static final String SERVER_IP = "localhost";  // địa chỉ IP
    private static final int SERVER_PORT = 1836;   // cổng
    private static SocketClient instance;   // biến để triển khai singleton
    private Socket socket;
    private ObjectOutputStream out;   // đẩy , ghi dữ liệu
    private ObjectInputStream in;   // lấy dữ liệu
    private final List<ResponseObserver> observers = new CopyOnWriteArrayList<>();   // arrayList bản chuẩn thread safe
    private SocketClient(){};
    private static class ClientHelper {     // class để triển khi singleton cho đa luồng
        private static final SocketClient instance = new SocketClient();
    }
    public static SocketClient getInstance(){
        return ClientHelper.instance;
    }
    public void getConnect(){
        if (socket != null && !socket.isClosed()) {
            return;
        }
        try {
            socket = new Socket(SERVER_IP,SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            Thread threadListener = new ServerListener();
            threadListener.setDaemon(true);    // tự động tắt khi tắt app.
            threadListener.start();
        } catch (IOException e) {
            System.err.println(" lỗi khi kết nối tới server");
        }
    }
    public void disconnect() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendRequest(Request request) {
        try {
            if (out != null) {
                out.reset();
                out.writeObject(request);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Lỗi gửi dữ liệu: " + e.getMessage());
        }
    }
    private class ServerListener extends Thread {   // class chạy ngầm để nhân Response từ server trả về.
        @Override
        public void run() {
            try {
                Object obj;
                while ((obj = in.readObject()) != null) {
                    // Kiểm tra nếu là chuẩn Response của hệ thống thì mới nhận
                    if (obj instanceof Response) {
                        Response response = (Response) obj;
                        notifyObservers(response);
                    }
                }
            } catch (EOFException e) {
                System.err.println("Mất kết nối với Server.");
            } catch (java.net.SocketException e) {
                if (socket.isClosed()) {
                    System.out.println("Kết nối mạng đã được ngắt an toàn do tắt ứng dụng.");
                } else {
                    System.err.println("Lỗi mất kết nối mạng đột ngột: " + e.getMessage());
                }
            } catch (Exception e) {
                System.err.println("Lỗi đọc luồng mạng: " + e.getMessage());
            }
        }
    }
    public void addObserver(ResponseObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    public void removeObservers(ResponseObserver current) {
        observers.remove(current);
    }
    private void notifyObservers(Response response) {
        Platform.runLater(() -> {
            // Copy list ra để tránh lỗi ConcurrentModificationException nếu có người unregister lúc đang duyệt
            List<ResponseObserver> safeObservers = new ArrayList<>(observers);
            for (ResponseObserver observer : safeObservers) {
                observer.onResponse(response);
            }
        });
    }
}
