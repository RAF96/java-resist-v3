package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class WorkerImpl implements Worker {
    private final ServerMetrics4 serverMetrics4;
    private final List<Double> list;
    private final ExecutorService writerTaskExecutorService;
    private final Socket socket;
    private WriterTask writerTask;

    public WorkerImpl(ServerMetrics4 serverMetrics4, List<Double> list, ExecutorService writerTaskExecutorService, Socket socket) {
        this.serverMetrics4 = serverMetrics4;
        this.list = list;
        this.writerTaskExecutorService = writerTaskExecutorService;
        this.socket = socket;
    }

    @Override
    public void run() {
        serverMetrics4.setRequestProcessingStart(System.currentTimeMillis());
        List<Double> doubleList = Sort.sort(list);
        serverMetrics4.setRequestProcessingEnd(System.currentTimeMillis());
        try {
            writerTask = WriterTask.create(socket, serverMetrics4, doubleList);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Future<?> future = writerTaskExecutorService.submit(writerTask);
        try {
            future.get();
        } catch (InterruptedException exception) {
            writerTaskExecutorService.shutdownNow();
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } catch (ExecutionException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public ServerMetrics4 getServerMetrics() {
        return writerTask.getServerMetrics4();
    }
}
