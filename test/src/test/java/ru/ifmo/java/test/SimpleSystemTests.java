package ru.ifmo.java.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonUserInterface.*;
import ru.ifmo.java.managingServer.ManagingServer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SimpleSystemTests {
    private final static Thread managingServerThread = new Thread(ManagingServer.create(), "managingServerThread");
    private static CommonUserInterface commonUserInterface;
    //TODO. uncomment
//    private final static List<ServerType> listOfTestedServer =
//            List.of(ServerType.INDIVIDUAL_THREAD_SERVER, ServerType.BLOCKING_THREAD_SERVER);
    private final static List<ServerType> listOfTestedServer = List.of(ServerType.BLOCKING_THREAD_SERVER);
//    private final static List<ServerType> listOfTestedServer = List.of(ServerType.INDIVIDUAL_THREAD_SERVER);


    @BeforeAll
    private static void runManagingServer() throws InterruptedException, IOException {
        managingServerThread.start();
        //FIXME. magic const
        Thread.sleep(1000);
        commonUserInterface = CommonUserInterface.create();
    }

    @AfterAll
    private static void haltManagingServer() {
        managingServerThread.interrupt();
    }

    private static void printMetrics(ServerType serverType, ServerPerformanceMetrics metrics) {
        System.out.print("serverType: ");
        System.out.println(serverType);
        System.out.print("requestProcessingTime: ");
        System.out.println(metrics.getRequestProcessingTime());
        System.out.print("requestClientTime: ");
        System.out.println(metrics.getClientProcessingTime());
        System.out.print("averageTimeSpendByClient: ");
        System.out.println(metrics.getAverageTimeSpendByClient());
    }

    private ServerPerformanceMetrics runSimpleTest(
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
        printMetrics(serverType, metrics);
        return metrics;
    }


    @RepeatedTest(2)
    public void runTestWithOneUserAndOneRequest() throws IOException, InterruptedException {
        for (ServerType serverType : listOfTestedServer) {
            runSimpleTest(serverType, 1, 5, 1, 0);
        }

    }

    @RepeatedTest(2)
    public void runTestWithOneUserAndTwoRequest() throws IOException, InterruptedException {
        for (ServerType serverType : listOfTestedServer) {
            runSimpleTest(serverType, 1, 5, 2, 0);
        }
    }


    @RepeatedTest(2)
    public void runTestTwoUsersAndOneRequest() throws IOException, InterruptedException {
        for (ServerType serverType : listOfTestedServer) {
            runSimpleTest(serverType, 2, 5, 1, 0);
        }
    }

    @RepeatedTest(2)
    public void runTestWithTwoUsersTwoRequests() throws IOException, InterruptedException {
        for (ServerType serverType : listOfTestedServer) {
            runSimpleTest(serverType, 2, 5, 2, 0);
        }
    }

    @RepeatedTest(2)
    public void runTestWithOneUserAndLargeMessage() throws IOException, InterruptedException {
        for (ServerType serverType : listOfTestedServer) {
            runSimpleTest(serverType, 1, 5000, 1, 0);
        }
    }

}
