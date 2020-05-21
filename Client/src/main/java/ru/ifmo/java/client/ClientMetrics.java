package ru.ifmo.java.client;

import java.util.List;
import java.util.OptionalDouble;

public interface ClientMetrics {
    static ClientMetrics create(int numberOfSentRequests, double averageTimeOfWork) {
        return new ClientMetrics() {
            @Override
            public double getAverageTimeSpendByClient() {
                return averageTimeOfWork;
            }

            @Override
            public int getNumberOfRequest() {
                return numberOfSentRequests;
            }
        };
    }

    static ClientMetrics average(List<ClientMetrics> list) {
        OptionalDouble optionalAverage = list.stream().mapToDouble(ClientMetrics::getAverageTimeSpendByClient).average();
        int sum = list.stream().mapToInt(ClientMetrics::getNumberOfRequest).sum();
        double average;
        if (optionalAverage.isEmpty()) {
            average = 0;
        } else {
            average = optionalAverage.getAsDouble();
        }
        return ClientMetrics.create(sum, average);
    }

    double getAverageTimeSpendByClient();

    int getNumberOfRequest();
}
