package ru.ifmo.java.managingServer;

import java.net.Socket;

public interface CommunicationHandler extends Runnable {
    static CommunicationHandler create(Socket socket) {
        return new CommunicationHandlerImpl(socket);
    }
}
