package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface WriterTask extends Runnable {

    static WriterTask create(Socket socket, ServerMetrics4 serverMetrics4, List<Double> list) throws IOException {
        return new WriterTaskImpl(socket, serverMetrics4, list);
    }

    ServerMetrics4 getServerMetrics4();
}
