package ru.java.ifmo.commonUserInterface;

import ru.ifmo.java.client.ClientMetrics;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.protocol.Protocol.*;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CommonUserInterfaceImpl implements CommonUserInterface {
    private Socket managingServerSocket;

    @Override
    public AggregateServerPerformanceMetrics runComplexTestingOfServerPerformance(SettingsOfComplexTestingOfServerPerformance settingsOfComplexTesting) {
        List<ServerPerformanceMetrics> list = new ArrayList<>();
        try {
            for (var settingsOfTesting : settingsOfComplexTesting) {
                list.add(testServerPerformance(settingsOfTesting));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return AggregateServerPerformanceMetrics.create(list);
    }

    private ServerPerformanceMetrics testServerPerformance(SettingsOfServerPerformanceTesting settings) throws IOException {
        runServer(settings);
        ClientMetrics clientMetrics = runClient(settings);
        ServerMetrics serverMetrics = getManagingServerResponse();
        return ServerPerformanceMetrics.create(serverMetrics.getRequestProcessingTime(), serverMetrics.getClientProcessingTime(), clientMetrics.getAverageTimeSpendByClient());
    }

    private ServerMetrics getManagingServerResponse() throws IOException {
        ResponseOfComputingServerStartup response = ResponseOfComputingServerStartup.parseDelimitedFrom(managingServerSocket.getInputStream());
        return ServerMetrics.create(response.getRequestProcessingTime(), response.getClientProcessingTime());
    }

    //TODO
    private ClientMetrics runClient(SettingsOfServerPerformanceTesting settings) {
        return null;
    }

    private void runServer(SettingsOfServerPerformanceTesting settings) throws IOException {
        managingServerSocket = new Socket(Constant.serverHost, Constant.managingServerPort);
        RequestOfComputingServerStartup.newBuilder()
                .setNumberOfClients(settings.getNumberOfClients())
                .setServerType(ServerType.serverType2ProtocolServerType(settings.getServerType()))
                .build().writeDelimitedTo(managingServerSocket.getOutputStream());
    }
}
