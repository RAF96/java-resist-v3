package ru.ifmo.java.client;

public interface ClientMetrics {
    static ClientMetrics create(int numberOfSendedRequests, double timeOfWork) {
        return () -> timeOfWork / numberOfSendedRequests;
    }
    double getAverageTimeSpendByClient();
}
