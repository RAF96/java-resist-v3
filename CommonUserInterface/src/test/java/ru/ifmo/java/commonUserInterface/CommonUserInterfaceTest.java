package ru.ifmo.java.commonUserInterface;

import org.junit.jupiter.api.Test;
import ru.ifmo.java.common.ServerType;

import java.io.IOException;
import java.util.Collections;


class CommonUserInterfaceTest {

    @Test
    void runComplexTestingOfServerPerformance() throws IOException {
        CommonUserInterface commonUserInterface = CommonUserInterface.create();
        SettingsOfComplexTestingOfServerPerformance settings = SettingsOfComplexTestingOfServerPerformance.create(
                Collections.singletonList(0),
                TypeOfVariableParameter.CLIENT_SLEEP_TIME,
                ServerType.INDIVIDUAL_THREAD_SERVER,
                2,
                1000,
                2,
                0
        );
        AggregateServerPerformanceMetrics metrics = commonUserInterface.runComplexTestingOfServerPerformance(settings);
    }
}