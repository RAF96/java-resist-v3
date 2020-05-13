package ru.ifmo.java.managingServer;

import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.computeServer.ComputeServerCreator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CommunicationHandlerImpl implements CommunicationHandler {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private Thread thread;
    private ComputeServer computeServer;

    public CommunicationHandlerImpl(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && !socket.isClosed()) {
                RequestOfComputingServer request;
                try {
                    request = RequestOfComputingServer.parseDelimitedFrom(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                switch (request.getOneOfCase()) {
                    case SERVERSTARTUP:
                        ServerType serverType = ServerType.protocolServerType2ServerType(request.getServerStartup().getServerType());
                        int numberOfClients = request.getServerStartup().getNumberOfClients();
                        runComputeServer(serverType, numberOfClients);
                        break;
                    case SERVERHALTING:
                        ServerMetrics serverMetrics;
                        try {
                            thread.interrupt();
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                        serverMetrics = computeServer.getServerMetrics();
                        try {
                            MetricsOfComputingServer.newBuilder()
                                    .setClientProcessingTime(serverMetrics.getClientProcessingTime())
                                    .setRequestProcessingTime(serverMetrics.getRequestProcessingTime())
                                    .build().writeDelimitedTo(outputStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        break;
                    default:
                        throw new RuntimeException("Unknown type of request");
                }
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void runComputeServer(ServerType serverType, int numberOfClients) {
        computeServer = ComputeServerCreator.newComputeServer(serverType, numberOfClients);
        thread = new Thread(computeServer, "computeServer");
        thread.start();
    }
}
