package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;


public class ClientHandlerImpl implements ClientHandler {
    private final ComputeServerSettings settings;
    private final Socket socket;
    private final ExecutorService workerExecutorService;
    private final CountDownLatch countDownLatch;
    private AverageServerMetrics averageServerMetrics;

    public ClientHandlerImpl(ComputeServerSettings settings, Socket socket, ExecutorService workerExecutorService, CountDownLatch countDownLatch) {
        this.settings = settings;
        this.socket = socket;
        this.workerExecutorService = workerExecutorService;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            work();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void work() throws InterruptedException, IOException {
        countDownLatch.countDown();
        countDownLatch.await();
        ReadAllRequestTask readAllRequestTask =
                ReadAllRequestTask.create(settings, countDownLatch, socket, workerExecutorService);
        readAllRequestTask.run();
        averageServerMetrics = readAllRequestTask.averageServerMetrics();
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public AverageServerMetrics getAverageServerMetrics() {
        return averageServerMetrics;
    }

}
