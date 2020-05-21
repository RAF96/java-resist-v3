package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.protocol.Protocol;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class WriterTaskImpl implements WriterTask {
    private final Socket socket;
    private final ServerMetrics4 serverMetrics4;
    private final List<Double> list;
    private final OutputStream outputStream;

    public WriterTaskImpl(Socket socket, ServerMetrics4 serverMetrics4, List<Double> list) throws IOException {
        this.socket = socket;
        outputStream = socket.getOutputStream();
        this.serverMetrics4 = serverMetrics4;
        this.list = list;
    }

    @Override
    public void run() {
        serverMetrics4.setClientProcessingEnd(System.currentTimeMillis());
        Protocol.MessageWithListOfDoubleVariables message = Protocol.MessageWithListOfDoubleVariables.newBuilder()
                .addAllNumber(list).build();
        byte[] bytes = MessageProcessing.packMessage(message.toByteArray());
        try {
            outputStream.write(bytes);
        } catch (IOException ignored) {
        }
    }

    @Override
    public ServerMetrics4 getServerMetrics4() {
        return serverMetrics4;
    }
}
