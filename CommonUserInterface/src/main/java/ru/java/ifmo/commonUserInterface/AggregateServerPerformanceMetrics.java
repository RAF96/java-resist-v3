package ru.java.ifmo.commonUserInterface;

import java.util.List;
import java.util.stream.Collectors;

public interface AggregateServerPerformanceMetrics {
    static AggregateServerPerformanceMetrics create(List<ServerPerformanceMetrics> list) {
        List<Double> requestProcessingTime = list.stream().
                map(ServerPerformanceMetrics::getRequestProcessingTime).collect(Collectors.toList());
        List<Double> clientProcessingTime = list.stream().
                map(ServerPerformanceMetrics::getClientProcessingTime).collect(Collectors.toList());
        List<Double> averageTimeSpendByClient = list.stream().
                map(ServerPerformanceMetrics::getAverageTimeSpendByClient).collect(Collectors.toList());
        return new AggregateServerPerformanceMetrics() {
            @Override
            public List<Double> getRequestProcessingTime() {
                return requestProcessingTime;
            }

            @Override
            public List<Double> getClientProcessingTime() {
                return clientProcessingTime;
            }

            @Override
            public List<Double> getAverageTimeSpendByClient() {
                return averageTimeSpendByClient;
            }
        };
    }

    List<Double> getRequestProcessingTime();

    List<Double> getClientProcessingTime();

    List<Double> getAverageTimeSpendByClient();
}
