package ru.ifmo.java.commonPartsOfComputeServer;

public interface ComputeServerSettings {
    static ComputeServerSettings create(int numberOfClients) {
        return () -> numberOfClients;
    }

    int getNumberOfClients();
}
