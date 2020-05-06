package ru.ifmo.java.managingServer;

import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.computeServer.ComputeServerCreator;

import java.io.IOException;
import java.net.Socket;

public class CommunicationHandlerImpl implements CommunicationHandler {
    private final Socket socket;

    public CommunicationHandlerImpl(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            RequestOfComputingServerStartup request;
            try {
                request = RequestOfComputingServerStartup.parseDelimitedFrom(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            ServerType serverType = ServerType.protocolServerType2ServerType(request.getServerType());
            ServerMetrics serverMetrics;
            try {
                serverMetrics = runComputeServer(serverType, request.getNumberOfClients());
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            ResponseOfComputingServerStartup response = ResponseOfComputingServerStartup.newBuilder()
                    .setRequestProcessingTime(serverMetrics.getRequestProcessingTime())
                    .setClientProcessingTime(serverMetrics.getClientProcessingTime())
                    .build();
            try {
                response.writeDelimitedTo(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private ServerMetrics runComputeServer(ServerType serverType, int numberOfClients) throws Exception {
        ComputeServer computeServer = ComputeServerCreator.newComputeServer(serverType, numberOfClients);
        return computeServer.call();
    }
}
