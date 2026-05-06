package uet.server;

import uet.server.networkServer.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 1836;
    private static final int MAX_CLIENT = 20;
    private ExecutorService threadPool;
    private ServerMain(){
        threadPool = Executors.newFixedThreadPool(20);
    }
    private void startServer(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true){
                Socket socketClient = serverSocket.accept();
//                ClientHandler clientHandler = new ClientHandler(socketClient);
//                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi chạy server");
        } finally {
            if (threadPool != null){
                threadPool.shutdown();
            }
        }
    }
    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain();
        serverMain.startServer();
    }
}
