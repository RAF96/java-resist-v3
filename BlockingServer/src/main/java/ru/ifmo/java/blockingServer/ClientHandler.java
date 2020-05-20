package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;

import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public interface ClientHandler extends Runnable {
    static ClientHandler create(ComputeServerSettings settings,
                                Socket socket,
                                ExecutorService clientHandlersExecutorService,
                                CountDownLatch countDownLatch) {
        return new ClientHandlerImpl(settings, socket, clientHandlersExecutorService, countDownLatch);
    }

    AverageServerMetrics getAverageServerMetrics();
}
