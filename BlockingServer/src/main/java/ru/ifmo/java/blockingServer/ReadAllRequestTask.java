package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public interface ReadAllRequestTask extends Runnable {

    static ReadAllRequestTask create(ComputeServerSettings settings,
                                     CountDownLatch countDownLatch, Socket socket, ExecutorService workersThread) throws IOException {
        return new ReadAllRequestTaskImpl(settings, countDownLatch, socket, workersThread);
    }

    AverageServerMetrics averageServerMetrics();
}
