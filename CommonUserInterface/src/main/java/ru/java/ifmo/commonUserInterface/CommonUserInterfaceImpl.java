package ru.java.ifmo.commonUserInterface;

import ru.ifmo.java.client.Client;
import ru.ifmo.java.client.ClientMetrics;
import ru.ifmo.java.client.ClientSettings;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.common.protocol.Protocol.RequestOfComputingServerStartup;
import ru.ifmo.java.common.protocol.Protocol.ResponseOfComputingServerStartup;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private ClientMetrics runClient(SettingsOfServerPerformanceTesting settings) {
        ExecutorService executorService = Executors.newFixedThreadPool(settings.getNumberOfClients());
        ClientSettings clientSettings = ClientSettings.create(settings.getServerType(),
                settings.getSizeOfRequest(),
                settings.getNumberOfRequestPerClient(),
                settings.getClientSleepTime());
        List<Future<ClientMetrics>> futures = new ArrayList<>();
        for (int index = 0; index < settings.getNumberOfClients(); index++) {
            Client client = Client.create(clientSettings);
            futures.add(executorService.submit(client));
        }
        List<ClientMetrics> clientMetricsList = new ArrayList<>();
        for (var future : futures) {
            // TODO. is it right way catching exception?
            ClientMetrics clientMetrics = null;
            boolean success = false;
            try {
                clientMetrics = future.get();
                success = true;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (success) {
                clientMetricsList.add(clientMetrics);
            }
        }
        OptionalDouble average = clientMetricsList.stream().mapToDouble(ClientMetrics::getAverageTimeSpendByClient).average();
        return ClientMetrics.create(average.orElse(0));
    }

    private void runServer(SettingsOfServerPerformanceTesting settings) throws IOException {
        managingServerSocket = new Socket(Constant.serverHost, Constant.managingServerPort);
        RequestOfComputingServerStartup.newBuilder()
                .setNumberOfClients(settings.getNumberOfClients())
                .setServerType(ServerType.serverType2ProtocolServerType(settings.getServerType()))
                .build().writeDelimitedTo(managingServerSocket.getOutputStream());
    }
}
