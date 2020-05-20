package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;

import java.util.concurrent.ExecutorService;

public interface ReadAllRequestTask extends Runnable {

    static ReadAllRequestTask create(ComputeServerSettings settings,
                                     ExecutorService workersThread) {
        return new ReadAllRequestTaskImpl(settings, workersThread);
    }

    AverageServerMetrics averageServerMetrics();
}
