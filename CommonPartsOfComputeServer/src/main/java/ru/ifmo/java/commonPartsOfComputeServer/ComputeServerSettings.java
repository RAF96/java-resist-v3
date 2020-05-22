package ru.ifmo.java.commonPartsOfComputeServer;

public interface ComputeServerSettings {
    static ComputeServerSettings create(int numberOfClients, int numberOfRequest) {
        return new ComputeServerSettings() {
            @Override
            public int getNumberOfClients() {
                return numberOfClients;
            }

            @Override
            public int getNumberOfRequests() {
                return numberOfRequest;
            }
        };
    }

    int getNumberOfClients();

    int getNumberOfRequests();
}
