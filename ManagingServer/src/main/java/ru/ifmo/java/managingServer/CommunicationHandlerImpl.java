package ru.ifmo.java.managingServer;

import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.computeServer.ComputeServerCreator;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommunicationHandlerImpl implements CommunicationHandler {
    private final Socket socket;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<ServerMetrics> future;

    public CommunicationHandlerImpl(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && !socket.isClosed()) {
                RequestOfComputingServer request;
                try {
                    request = RequestOfComputingServer.parseDelimitedFrom(socket.getInputStream());
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
                            serverMetrics = haltComputeServer();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                        try {
                            MetricsOfComputingServer.newBuilder()
                                    .setClientProcessingTime(serverMetrics.getClientProcessingTime())
                                    .setRequestProcessingTime(serverMetrics.getRequestProcessingTime())
                                    .build().writeDelimitedTo(socket.getOutputStream());
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

    private ServerMetrics haltComputeServer() throws ExecutionException, InterruptedException {
        future.cancel(true);
        return future.get();
    }

    private void runComputeServer(ServerType serverType, int numberOfClients) {
        ComputeServer computeServer = ComputeServerCreator.newComputeServer(serverType, numberOfClients);
        future = executorService.submit(computeServer);
    }
}
