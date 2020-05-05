package ru.ifmo.java.managingServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ManagingServerSocketLoopImpl implements ManagingServerSocketLoop {
    private final ServerSocket serverSocket;

    public ManagingServerSocketLoopImpl(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            CommunicationHandler.create(socket).run();
        }
    }
}
