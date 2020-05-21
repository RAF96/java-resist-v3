package ru.ifmo.java.commonUserInterface;

import ru.ifmo.java.client.Client;
import ru.ifmo.java.client.ClientMetrics;
import ru.ifmo.java.client.ClientSettings;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class CommonUserInterfaceImpl implements CommonUserInterface {
    private final Socket managingServerSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public CommonUserInterfaceImpl() throws IOException {
        managingServerSocket = new Socket(Constant.serverHost, Constant.managingServerPort);
        inputStream = managingServerSocket.getInputStream();
        outputStream = managingServerSocket.getOutputStream();
    }

    @Override
    public AggregateServerPerformanceMetrics runComplexTestingOfServerPerformance(SettingsOfComplexTestingOfServerPerformance settingsOfComplexTesting) throws InterruptedException, IOException {
        List<ServerPerformanceMetrics> list = new ArrayList<>();
        for (var settingsOfTesting : settingsOfComplexTesting) {
            list.add(runTestingOfServerPerformance(settingsOfTesting));
        }
        return AggregateServerPerformanceMetrics.create(list);
    }

    @Override
    public ServerPerformanceMetrics runTestingOfServerPerformance(SettingsOfServerPerformanceTesting settings) throws IOException, InterruptedException {
        runServer(settings);
        //FIXME. Magic const
        Thread.sleep(1000);
        ClientMetrics clientMetrics = runClients(settings);
        ServerMetrics serverMetrics = getManagingServerResponse();
        return ServerPerformanceMetrics.create(serverMetrics.getRequestProcessingTime(), serverMetrics.getClientProcessingTime(),
                clientMetrics.getAverageTimeSpendByClient(), clientMetrics.getNumberOfRequest());
    }

    private void runServer(SettingsOfServerPerformanceTesting settings) throws IOException {
        RequestOfComputingServerStartup request = RequestOfComputingServerStartup.newBuilder()
                .setNumberOfClients(settings.getNumberOfClients())
                .setNumberOfRequests(settings.getNumberOfRequestPerClient())
                .setServerType(ServerType.serverType2ProtocolServerType(settings.getServerType()))
                .build();
        RequestOfComputingServer.newBuilder().setServerStartup(request).build()
                .writeDelimitedTo(outputStream);
    }

    private ClientMetrics runClients(SettingsOfServerPerformanceTesting settings)
            throws InterruptedException, IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(settings.getNumberOfClients());
        CompletionService<ClientMetrics> completionService = new ExecutorCompletionService<>(executorService);
        ClientSettings clientSettings = ClientSettings.create(
                settings.getSizeOfRequest(),
                settings.getNumberOfRequestPerClient(),
                settings.getClientSleepTime());

        for (int index = 0; index < settings.getNumberOfClients(); index++) {
            Client client = Client.create(clientSettings);
            completionService.submit(client);
        }
        Future<ClientMetrics> future = completionService.take();
        ClientMetrics firstClientMetrics;
        try {
            firstClientMetrics = future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            haltServer();
        }

        List<ClientMetrics> clientMetricsList = new ArrayList<>(
                Collections.singletonList(firstClientMetrics)
        );
        for (int index = 0; index < settings.getNumberOfClients() - 1; index++) {
            future = completionService.take();
            ClientMetrics clientMetrics;
            try {
                clientMetrics = future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            clientMetricsList.add(clientMetrics);
        }
        return ClientMetrics.average(clientMetricsList);
    }

    private void haltServer() throws IOException {
        RequestOfHaltingOfComputingServer request = RequestOfHaltingOfComputingServer.newBuilder().build();
        RequestOfComputingServer.newBuilder().setServerHalting(request).build()
                .writeDelimitedTo(outputStream);
    }

    private ServerMetrics getManagingServerResponse() throws IOException {
        MetricsOfComputingServer response = MetricsOfComputingServer.parseDelimitedFrom(inputStream);
        return ServerMetrics.create(response.getRequestProcessingTime(), response.getClientProcessingTime());
    }

}
