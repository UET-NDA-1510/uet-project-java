package uet.server;

import uet.server.DAO.DBConnection;
import uet.server.networkServer.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 1836;
    private static final int MAX_CLIENT = 20;
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private ServerMain(){
        threadPool = Executors.newFixedThreadPool(20);
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
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Khởi động server");
            listenForShutdown();
            while (true){
                Socket socketClient = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socketClient);
                threadPool.execute(clientHandler);
            }
        }catch (SocketException e) {
            System.out.println("Đã ngừng nhận kết nối mới.");}
        catch (IOException e) {
            System.err.println("Lỗi khi chạy server");
        } finally {
            threadPool.shutdown();
            DBConnection.closePool();
            System.out.println("Đã đóng server.");
        }
    }
    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain();
        serverMain.startServer();
    }
}
