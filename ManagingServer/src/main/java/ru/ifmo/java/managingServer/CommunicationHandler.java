package ru.ifmo.java.managingServer;

import java.io.IOException;
import java.net.Socket;

public interface CommunicationHandler extends Runnable {
    static CommunicationHandler create(Socket socket) throws IOException {
        return new CommunicationHandlerImpl(socket);
    }
}
