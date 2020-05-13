package ru.ifmo.java.client;

public interface ClientMetrics {
    static ClientMetrics create(int numberOfSentRequests, double timeOfWork) {
        return () -> timeOfWork / numberOfSentRequests;
    }


    static ClientMetrics create(double averageTimeSpendByClient) {
        return () -> averageTimeSpendByClient;
    }

    double getAverageTimeSpendByClient();
}
