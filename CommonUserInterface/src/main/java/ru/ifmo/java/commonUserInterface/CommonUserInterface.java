package ru.ifmo.java.commonUserInterface;

import java.io.IOException;

public interface CommonUserInterface {

    static CommonUserInterface create() throws IOException {
        return new CommonUserInterfaceImpl();
    }

    AggregateServerPerformanceMetrics runComplexTestingOfServerPerformance(SettingsOfComplexTestingOfServerPerformance settings) throws InterruptedException, IOException;

    ServerPerformanceMetrics runTestingOfServerPerformance(SettingsOfServerPerformanceTesting settings) throws IOException, InterruptedException;

    void clear();
}
