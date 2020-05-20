package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;

import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

//TODO
public class ClientHandlerImpl implements ClientHandler {
    private final ComputeServerSettings settings;
    private final Socket socket;
    private final ExecutorService clientHandlersExecutorService;
    private final CountDownLatch countDownLatch;
    private AverageServerMetrics averageServerMetrics;

    public ClientHandlerImpl(ComputeServerSettings settings, Socket socket, ExecutorService clientHandlersExecutorService, CountDownLatch countDownLatch) {
        this.settings = settings;
        this.socket = socket;
        this.clientHandlersExecutorService = clientHandlersExecutorService;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        countDownLatch.countDown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public AverageServerMetrics getAverageServerMetrics() {
        return averageServerMetrics;
    }

}
