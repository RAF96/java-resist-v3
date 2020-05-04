package ru.java.ifmo.commonUserInterface;

import ru.ifmo.java.client.ClientMetrics;

import java.util.ArrayList;
import java.util.List;

public class CommonUserInterfaceImpl implements CommonUserInterface {
    @Override
    public AggregateServerPerformanceMetrics runComplexTestingOfServerPerformance(SettingsOfComplexTestingOfServerPerformance settingsOfComplexTesting) {
        List<ServerPerformanceMetrics> list = new ArrayList<>();
        for (var settingsOfTesting : settingsOfComplexTesting) {
            list.add(testServerPerformance(settingsOfTesting));
        }
        return AggregateServerPerformanceMetrics.create(list);
    }

    //TODO
    private ServerPerformanceMetrics testServerPerformance(SettingsOfServerPerformanceTesting settings) {
        runServer(settings);
        ClientMetrics clientMetrics = runClient(settings);
        ServerMetrics serverMetrics = getManagingServerResponse();
        return null;
    }

    //TODO
    private ServerMetrics getManagingServerResponse() {
        return null;
    }

    //TODO
    private ClientMetrics runClient(SettingsOfServerPerformanceTesting settings) {
        return null;
    }

    //TODO
    private void runServer(SettingsOfServerPerformanceTesting settings) {

    }
}
