package ru.ifmo.java.client;

public interface ClientMetrics {
    static ClientMetrics create(int numberOfSendedRequests, double timeOfWork) {
        return () -> timeOfWork / numberOfSendedRequests;
    }


    static ClientMetrics create(double averageTimeSpendByClient) {
        return () -> averageTimeSpendByClient;
    }

    double getAverageTimeSpendByClient();
}
