package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public interface Worker extends Callable<List<ServerMetrics>> {
    static Worker create(Socket socket, CountDownLatch latch, ComputeServerSettings computeServerSettings) {
        return new WorkerImpl(socket, latch, computeServerSettings);
    }
}
