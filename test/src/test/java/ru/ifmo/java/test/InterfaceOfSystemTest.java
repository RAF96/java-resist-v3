package ru.ifmo.java.test;

import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonUserInterface.CommonUserInterface;
import ru.ifmo.java.commonUserInterface.ServerPerformanceMetrics;
import ru.ifmo.java.commonUserInterface.SettingsOfServerPerformanceTesting;

import java.io.IOException;
import java.util.List;

public interface InterfaceOfSystemTest {

    List<ServerType> listOfTestedServer =
            List.of(ServerType.INDIVIDUAL_THREAD_SERVER,
                    ServerType.BLOCKING_THREAD_SERVER,
                    ServerType.NOT_BLOCKING_THREAD_SERVER);
//    List<ServerType> listOfTestedServer = List.of(ServerType.INDIVIDUAL_THREAD_SERVER);
//    List<ServerType> listOfTestedServer = List.of(ServerType.BLOCKING_THREAD_SERVER);
//    List<ServerType> listOfTestedServer = List.of(ServerType.NOT_BLOCKING_THREAD_SERVER);


    default void printMetrics(SettingsOfServerPerformanceTesting serverSettings, ServerPerformanceMetrics metrics) {
        System.out.print("serverType: ");
        System.out.println(serverSettings.getServerType());
        System.out.println(String.format("Whole number of requests: %d, successful number of requests %d",
                serverSettings.getNumberOfClients() * serverSettings.getNumberOfRequestPerClient(),
                metrics.getNumberOfSuccessfulRequests()));
        System.out.print("requestProcessingTime: ");
        System.out.println(metrics.getRequestProcessingTime());
        System.out.print("requestClientTime: ");
        System.out.println(metrics.getClientProcessingTime());
        System.out.print("averageTimeSpendByClient: ");
        System.out.println(metrics.getAverageTimeSpendByClient());
    }


    default ServerPerformanceMetrics runSimpleTest(
            CommonUserInterface commonUserInterface,
            ServerType serverType,
            int numberOfClients,
            int sizeOfRequest,
            int numberOfRequestPerClient,
            int clientSleepTime
    ) throws IOException, InterruptedException {
        SettingsOfServerPerformanceTesting settings = SettingsOfServerPerformanceTesting.create(
                serverType,
                numberOfClients,
                sizeOfRequest,
                numberOfRequestPerClient,
                clientSleepTime
        );
        ServerPerformanceMetrics metrics = commonUserInterface.runTestingOfServerPerformance(settings);
        printMetrics(settings, metrics);
        return metrics;
    }
}
