package ru.java.ifmo.commonUserInterface;

import java.io.IOException;

public interface CommonUserInterface {

    static CommonUserInterface create() throws IOException {
        return new CommonUserInterfaceImpl();
    }

    AggregateServerPerformanceMetrics runComplexTestingOfServerPerformance(SettingsOfComplexTestingOfServerPerformance settings);
}
