package uet.server;

import uet.common.model.Auction.Auction;
import uet.common.payLoad.Response;
import uet.server.DAO.DBConnection;
import uet.server.networkServer.AuctionScheduler;
import uet.server.networkServer.ClientHandler;
import uet.server.networkServer.restoreAuction;
import uet.server.service.auctionService.AuctionService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 1836;
    private static final int MAX_CLIENT = 20;
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    public static final List<ClientHandler> onlineClients = new CopyOnWriteArrayList<>();
    private ServerMain(){
        threadPool = Executors.newFixedThreadPool(MAX_CLIENT);
    }
    private void listenForShutdown() {
        Thread shutdownThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input.trim())) {
                    System.out.println("Đang tắt server...");
                    try {
                        if (serverSocket != null && !serverSocket.isClosed()) {
                            serverSocket.close();
                        }
                    } catch (IOException e) {
                        System.err.println("Lỗi khi đóng server: " + e.getMessage());
                    }
                    break;
                }
            }
            scanner.close();
        });
        shutdownThread.setDaemon(true); // tự tắt khi main thread kết thúc
        shutdownThread.start();
    }
    private void startServer(){
        try {
            restoreAuction.restoreAuctionCache();
            List<Auction> activeAuctions = AuctionService.getInstance().getActiveAuctions();
            for (Auction a : activeAuctions) {
                AuctionScheduler.getInstance().scheduleAuctionEvents(a);
            }
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Khởi động server");
            listenForShutdown();
            while (true) {
                Socket socketClient = serverSocket.accept();
                socketClient.setTcpNoDelay(true);
                ClientHandler clientHandler = new ClientHandler(socketClient);
                onlineClients.add(clientHandler);
                threadPool.execute(clientHandler);
            }
        }catch (SQLException e){
            System.err.println("Lỗi khi lấy activeAuctions");
        }catch (SocketException e) {
            System.out.println("Đã ngừng nhận kết nối mới.");}
        catch (IOException e) {
            System.err.println("Lỗi khi chạy server");
        } finally {
            AuctionScheduler.getInstance().shutdown();
            threadPool.shutdown();
            DBConnection.closePool();
            System.out.println("Đã đóng server.");
        }
    }
    public static void broadcast(Response response) {
        for (ClientHandler client : onlineClients) {
            try {
                client.sendResponse(response);
            } catch (IOException e) {
                // Nếu gửi lỗi (client đã ngắt mạng đột ngột) thì xóa khỏi list
                onlineClients.remove(client);
            }
        }
    }
    public static void broadcastToTargetUsers(List<Long> targetUserID, Response response) {
        if (targetUserID == null || targetUserID.isEmpty()) return;
        for (ClientHandler client : onlineClients) {
            Long clientID = client.getLoggedInUserID();
            // Nếu Client đã đăng nhập VÀ tên nằm trong danh sách cần gửi
            if (clientID != null && targetUserID.contains(clientID)) {
                try {
                    client.sendResponse(response);
                } catch (Exception e) {
                    System.err.println("Mất kết nối tới client: " + clientID);
                    onlineClients.remove(client);
                }
            }
        }

    }
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        ServerMain serverMain = new ServerMain();
        serverMain.startServer();
    }
}
