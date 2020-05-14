package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public interface Worker extends Runnable {
    static Worker create(Socket socket, CountDownLatch latch, ComputeServerSettings computeServerSettings) throws IOException {
        return new WorkerImpl(socket, latch, computeServerSettings);
    }

    AverageServerMetrics getAverageServerMetrics();
}
