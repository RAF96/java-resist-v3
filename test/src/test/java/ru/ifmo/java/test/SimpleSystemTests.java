package ru.ifmo.java.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonUserInterface.CommonUserInterface;
import ru.ifmo.java.commonUserInterface.ServerPerformanceMetrics;
import ru.ifmo.java.managingServer.ManagingServer;

import java.io.IOException;

public class SimpleSystemTests implements InterfaceOfSystemTest {
    private final static Thread managingServerThread = new Thread(ManagingServer.create(), "managingServerThread");
    private static CommonUserInterface commonUserInterface;


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
        try {
            managingServerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commonUserInterface.clear();
    }

    private ServerPerformanceMetrics runSimpleTest(
            ServerType serverType,
            int numberOfClients,
            int sizeOfRequest,
            int numberOfRequestPerClient,
            int clientSleepTime
    ) throws IOException, InterruptedException {
        return runSimpleTest(commonUserInterface,
                serverType,
                numberOfClients,
                sizeOfRequest,
                numberOfRequestPerClient,
                clientSleepTime);
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

    @RepeatedTest(2)
    public void runTestWhereCanWholeNumberOfRequestsNotEqualSuccessfulNumberOfRequests() throws IOException, InterruptedException {
        for (ServerType serverType : listOfTestedServer) {
            runSimpleTest(serverType, 10, 500, 10, 0);
        }
    }


}
