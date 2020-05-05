package ru.ifmo.java.managingServer;

import java.net.ServerSocket;

public interface ManagingServerSocketLoop extends Runnable {
    static ManagingServerSocketLoop create(ServerSocket serverSocket)  {
        return new ManagingServerSocketLoopImpl(serverSocket);
    }
}
