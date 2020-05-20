package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.net.Socket;
import java.util.List;

//TODO
public class WriterTaskImpl implements WriterTask {
    public WriterTaskImpl(Socket socket, ServerMetrics4 serverMetrics4, List<Double> list) {
    }

    @Override
    public void run() {

    }

    @Override
    public ServerMetrics4 getServerMetrics4() {
        return null;
    }
}
