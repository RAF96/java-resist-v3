package ru.ifmo.java.blockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;

import java.util.concurrent.ExecutorService;

//TODO
public class ReadAllRequestTaskImpl implements ReadAllRequestTask {
    public ReadAllRequestTaskImpl(ComputeServerSettings settings, ExecutorService workersThread) {
    }

    @Override
    public AverageServerMetrics averageServerMetrics() {
        return null;
    }

    @Override
    public void run() {

    }
}
