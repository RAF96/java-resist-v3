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

public class SystemTests {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static Future<?> futureOfManagingServer;

    @BeforeAll
    private static void runManagingServer() throws InterruptedException {
        futureOfManagingServer = executorService.submit(ManagingServer.create());
        //FIXME. magic const
        Thread.sleep(1000);
    }

    @AfterAll
    private static void haltManagingServer() {
        futureOfManagingServer.cancel(true);
    }

    @Test
    public void runSimpleTestOfComplexTestingOfServerPerformance() throws IOException {
        CommonUserInterface commonUserInterface = CommonUserInterface.create();
        SettingsOfComplexTestingOfServerPerformance settings = SettingsOfComplexTestingOfServerPerformance.create(
                Collections.singletonList(0),
                TypeOfVariableParameter.CLIENT_SLEEP_TIME,
                ServerType.INDIVIDUAL_THREAD_SERVER,
                1,
                1000,
                1,
                0
        );
        AggregateServerPerformanceMetrics metrics = commonUserInterface.runComplexTestingOfServerPerformance(settings);
    }
}
