package ru.ifmo.java.managingServer;

import ru.ifmo.java.common.Constant;

import java.io.IOException;
import java.net.ServerSocket;

public class ManagingServerImpl implements ManagingServer {

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Constant.managingServerPort);
            Thread thread = new Thread(ServerSocketLoop.create(serverSocket));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
