package ru.ifmo.java.managingServer;

import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

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
            ServerMetrics serverMetrics = runComputeServer(serverType,
                    request.getNumberOfClients());
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

    //TODO
    private ServerMetrics runComputeServer(ServerType serverType, int numberOfClients) {
        return null;
    }
}
