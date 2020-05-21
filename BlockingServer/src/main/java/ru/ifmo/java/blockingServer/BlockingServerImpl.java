package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingServerImpl implements BlockingServer {

    private final ComputeServerSettings computeServerSettings;
    private final ExecutorService clientHandlersExecutorService = Executors.newFixedThreadPool(Constant.numberOfThreads);
    private final CountDownLatch countDownLatch;
    private ServerMetrics serverMetrics;
    private final List<ClientHandler> clientHandlers = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();


    public BlockingServerImpl(ComputeServerSettings computeServerSettings) {
        this.computeServerSettings = computeServerSettings;
        countDownLatch = new CountDownLatch(computeServerSettings.getNumberOfClients());
    }

    @Override
    public ServerMetrics getServerMetrics() {
        return serverMetrics;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Constant.computeServerPort)) {
            acceptRequestsFromClients(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            joinAllHandler();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void acceptRequestsFromClients(ServerSocket serverSocket) throws IOException {
        for (int index = 0; index < computeServerSettings.getNumberOfClients(); index++){
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = ClientHandler.create(computeServerSettings,
                    socket,
                    clientHandlersExecutorService,
                    countDownLatch);
            Thread thread = new Thread(clientHandler, "clientHandlerOfBlockingServer");
            thread.start();
            clientHandlers.add(clientHandler);
            threads.add(thread);
        }
    }


    private void joinAllHandler() throws InterruptedException {
        List<AverageServerMetrics> serverMetricsList = new ArrayList<>();
        for (int index = 0; index < computeServerSettings.getNumberOfClients(); index++) {
            Thread thread = threads.get(index);
            ClientHandler clientHandler = clientHandlers.get(index);
            if (Thread.interrupted()) {
                thread.interrupt();
            }
            //FIXME. may interrupt here
            try {
                thread.join();
            } catch (InterruptedException ignored) {
                thread.join();
            }
            serverMetricsList.add(clientHandler.getAverageServerMetrics());
        }
        serverMetrics = AverageServerMetrics.average(serverMetricsList);
    }

}
