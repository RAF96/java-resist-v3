package ru.ifmo.java.client;


import org.junit.jupiter.api.*;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.protocol.Protocol;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class ClientTest {

    private static class ServerMock implements Runnable {

        private final int numberClients;
        private final int numberRequest;

        public ServerMock(int numberClients, int numberRequest) {

            this.numberClients = numberClients;
            this.numberRequest = numberRequest;
        }

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(Constant.computeServerPort)) {
                for (int indexServerSocket = 0; indexServerSocket < numberClients && !Thread.interrupted(); ++indexServerSocket) {
                    try(Socket socket = serverSocket.accept()) {
                        for (int indexSocket = 0; indexSocket < numberRequest && !socket.isClosed() && !Thread.interrupted(); ++indexSocket) {
                            byte[] input;
                            try {
                                input = MessageProcessing.readPackedMessage(socket.getInputStream());
                            } catch (MessageProcessing.ClosedSocket e) {
                                break;
                            }
                            List<Double> numberList = new ArrayList<>(Protocol.MessageWithListOfDoubleVariables
                                    .parseFrom(input).getNumberList());
                            Collections.sort(numberList);
                            byte[] output = Protocol.MessageWithListOfDoubleVariables.newBuilder()
                                    .addAllNumber(numberList)
                                    .build().toByteArray();
                            socket.getOutputStream().write(MessageProcessing.packMessage(output));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Thread thread;

    void runMockOfServer(int numberClients, int numberRequest) {
        thread = new Thread(new ServerMock(numberClients, numberRequest), "ServerMock");
        thread.start();
    }

    void haltMockOfServer() throws InterruptedException {
        thread.interrupt();
        thread.join();
    }

    @RepeatedTest(2)
    void runOneClient() throws Exception {
        int numberOfClients = 1;
        int numberOfRequest = 2;
        runMockOfServer(numberOfClients, numberOfRequest);
        ClientSettings clientSettings = ClientSettings.create(1, numberOfRequest, 0);
        Client client = Client.create(clientSettings);
        ClientMetrics metrics = client.call();
        assertNotNull(metrics);
    }

}