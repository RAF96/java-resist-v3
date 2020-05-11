package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class IndividualThreadServer implements ComputeServer {

    private final ComputeServerSettings serverSettings;

    public IndividualThreadServer(ComputeServerSettings serverSettings) {
        this.serverSettings = serverSettings;
    }

    @Override
    public ServerMetrics call() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(serverSettings.getNumberOfClients());
        List<Future<List<ServerMetrics>>> list = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(serverSettings.getNumberOfClients());
        List<Worker> workers = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(Constant.computeServerPort))
        {
            for (int index = 0; index < serverSettings.getNumberOfClients() &&
                    !Thread.interrupted(); index++) {
                Socket socket = serverSocket.accept();
                workers.add(Worker.create(socket, latch, serverSettings));
            }
        }
        List<Future<List<ServerMetrics>>> futures = null;
        try {
            futures = executorService.invokeAll(workers);
        } catch (InterruptedException ignored) {
        }
        List<ServerMetrics> serverMetricsList = new ArrayList<>();
        for (Future<List<ServerMetrics>> future : futures) {
            List<ServerMetrics> serverMetrics = future.get();
            serverMetricsList.addAll(serverMetrics);
        }
        double requestProcessingTime = serverMetricsList.stream()
                .mapToDouble(ServerMetrics::getRequestProcessingTime).average().orElseThrow();
        double clientProcessingTime = serverMetricsList.stream()
                .mapToDouble(ServerMetrics::getClientProcessingTime).average().orElseThrow();
        return ServerMetrics.create(requestProcessingTime, clientProcessingTime);
    }
}
