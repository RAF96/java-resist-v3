package ru.ifmo.java.managingServer;

import ru.ifmo.java.common.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManagingServerImpl implements ManagingServer {

    @Override
    public void run() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (ServerSocket serverSocket = new ServerSocket(Constant.managingServerPort)) {
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                executorService.submit(CommunicationHandler.create(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
