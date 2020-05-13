package ru.ifmo.java.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonUserInterface.AggregateServerPerformanceMetrics;
import ru.ifmo.java.commonUserInterface.CommonUserInterface;
import ru.ifmo.java.commonUserInterface.SettingsOfComplexTestingOfServerPerformance;
import ru.ifmo.java.commonUserInterface.TypeOfVariableParameter;
import ru.ifmo.java.managingServer.ManagingServer;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LargeSystemTests {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static Future<?> futureOfManagingServer;
    private static CommonUserInterface commonUserInterface;

    @BeforeAll
    private static void runManagingServer() throws InterruptedException, IOException {
        futureOfManagingServer = executorService.submit(ManagingServer.create());
        commonUserInterface = CommonUserInterface.create();
        //FIXME. magic const
        Thread.sleep(1000);
    }

    @AfterAll
    private static void haltManagingServer() {
        futureOfManagingServer.cancel(true);
    }

    @Test
    public void runSimpleTestWithOneUser() throws IOException {
        SettingsOfComplexTestingOfServerPerformance settings = SettingsOfComplexTestingOfServerPerformance.create(
                Collections.singletonList(0),
                TypeOfVariableParameter.CLIENT_SLEEP_TIME,
                ServerType.INDIVIDUAL_THREAD_SERVER,
                10,
                5000,
                10,
                0
        );
        AggregateServerPerformanceMetrics metrics = commonUserInterface.runComplexTestingOfServerPerformance(settings);
        printMetrics(metrics);
    }

    private void printMetrics(AggregateServerPerformanceMetrics metrics) {
        System.out.println(metrics.getRequestProcessingTime());
        System.out.println(metrics.getClientProcessingTime());
        System.out.println(metrics.getAverageTimeSpendByClient());
    }
}
