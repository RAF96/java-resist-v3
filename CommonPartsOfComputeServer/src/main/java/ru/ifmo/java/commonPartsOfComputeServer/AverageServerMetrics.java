package ru.ifmo.java.commonPartsOfComputeServer;

import java.util.List;

public interface AverageServerMetrics extends ServerMetrics {
    static AverageServerMetrics create(double sumRequestsProcessingTime,
                                       double sumClientsProcessingTime,
                                       int numberOfData) {
        return new AverageServerMetrics() {
            @Override
            public int numberOfData() {
                return numberOfData;
            }

            @Override
            public double getRequestProcessingTime() {
                return sumRequestsProcessingTime / numberOfData();
            }

            @Override
            public double getClientProcessingTime() {
                return sumClientsProcessingTime / numberOfData();
            }
        };
    }

    static AverageServerMetrics average(List<AverageServerMetrics> list) {
        int numberOfData = 0;
        double requestProcessingTime = 0;
        double clientProcessingTime = 0;
        for (AverageServerMetrics metrics : list) {
            numberOfData += metrics.numberOfData();
            requestProcessingTime += metrics.getRequestProcessingTime() * metrics.numberOfData();
            clientProcessingTime += metrics.getClientProcessingTime() * metrics.numberOfData();
        }
        return AverageServerMetrics.create(requestProcessingTime,
                clientProcessingTime,
                numberOfData);
    }

    int numberOfData();
}
