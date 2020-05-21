package ru.ifmo.java.commonUserInterface;

public interface ServerPerformanceMetrics {

    static ServerPerformanceMetrics create(double requestProcessingTime,
                                           double clientProcessingTime,
                                           double averageTimeSpendByClient,
                                           int numberOfSuccessfulRequests) {
        return new ServerPerformanceMetrics() {
            @Override
            public double getRequestProcessingTime() {
                return requestProcessingTime;
            }

            @Override
            public double getClientProcessingTime() {
                return clientProcessingTime;
            }

            @Override
            public double getAverageTimeSpendByClient() {
                return averageTimeSpendByClient;
            }

            @Override
            public int getNumberOfSuccessfulRequests() {
                return numberOfSuccessfulRequests;
            }
        };
    }

    double getRequestProcessingTime();

    double getClientProcessingTime();

    double getAverageTimeSpendByClient();

    int getNumberOfSuccessfulRequests();
}
