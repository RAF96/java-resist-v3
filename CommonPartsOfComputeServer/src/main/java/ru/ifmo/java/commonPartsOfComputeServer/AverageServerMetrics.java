package ru.ifmo.java.commonPartsOfComputeServer;

import java.util.List;

public interface AverageServerMetrics extends ServerMetrics {
    static AverageServerMetrics create(double requestProcessingTime,
                                       double clientProcessingTime,
                                       int numberOfData) {
        return new AverageServerMetrics() {
            @Override
            public int numberOfData() {
                return numberOfData;
            }

            @Override
            public double getRequestProcessingTime() {
                return requestProcessingTime / numberOfData();
            }

            @Override
            public double getClientProcessingTime() {
                return clientProcessingTime / numberOfData();
            }
        };
    }

    static AverageServerMetrics average(List<AverageServerMetrics> list) {
        int numberOfData = 0;
        double requestProcessingTime = 0;
        double clientProcessingTime = 0;
        for (AverageServerMetrics metrics : list) {
            numberOfData += metrics.numberOfData();
            requestProcessingTime += metrics.getRequestProcessingTime() * numberOfData;
            clientProcessingTime += metrics.getClientProcessingTime() * numberOfData;
        }
        return AverageServerMetrics.create(requestProcessingTime,
                clientProcessingTime,
                numberOfData);
    }

    int numberOfData();
}
