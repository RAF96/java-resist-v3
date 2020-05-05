package ru.ifmo.java.computeServer;

public interface ServerMetrics {
    static ServerMetrics create(double requestProcessingTime, double clientProcessingTime) {
        return new ServerMetrics() {

            @Override
            public double getRequestProcessingTime() {
                return requestProcessingTime;
            }

            @Override
            public double getClientProcessingTime() {
                return clientProcessingTime;
            }
        };
    }

    double getRequestProcessingTime();

    double getClientProcessingTime();
}
