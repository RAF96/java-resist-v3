package ru.ifmo.java.commonPartsOfComputeServer;

public interface ComputeServer extends Runnable {
    ServerMetrics getServerMetrics();
}
