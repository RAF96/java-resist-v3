package ru.ifmo.java.managingServer;

import java.net.ServerSocket;

public interface ServerSocketLoop extends Runnable {
    static ServerSocketLoop create(ServerSocket socket) {
        return new ServerSocketLoopImpl(socket);
    }
}
