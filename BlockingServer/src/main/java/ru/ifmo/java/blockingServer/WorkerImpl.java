package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

//TODO
public class WorkerImpl implements Worker {
    public WorkerImpl(ServerMetrics4 serverMetrics4, List<Double> list, ExecutorService executorService, Function<List<Double>, Runnable> getWriterTask) {
    }

    @Override
    public void run() {

    }

    @Override
    public ServerMetrics4 getServerMetrics() {
        return null;
    }
}
