package ru.ifmo.java.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonUserInterface.CommonUserInterface;
import ru.ifmo.java.managingServer.ManagingServer;

import java.io.IOException;

public class OtherSystemTests implements InterfaceOfSystemTest {
    private final static Thread managingServerThread = new Thread(ManagingServer.create(), "managingServerThread");

    @BeforeAll
    public static void runServer() {
        managingServerThread.start();
    }

    @AfterAll
    public static void haltServer() {
        managingServerThread.interrupt();
        try {
            managingServerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RepeatedTest(2)
    public void runTestImitateReopenCommonUserInterface() throws IOException, InterruptedException {
        for (ServerType serverType : listOfTestedServer) {
            CommonUserInterface commonUserInterface1 = CommonUserInterface.create();
            runSimpleTest(commonUserInterface1, serverType, 10, 500, 10, 0);
            commonUserInterface1.clear();
            CommonUserInterface commonUserInterface2 = CommonUserInterface.create();
            runSimpleTest(commonUserInterface2, serverType, 10, 500, 10, 0);
            commonUserInterface2.clear();
        }
    }
}
