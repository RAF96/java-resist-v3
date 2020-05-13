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
    public AggregateServerPerformanceMetrics runComplexTestingOfServerPerformance(SettingsOfComplexTestingOfServerPerformance settingsOfComplexTesting) {
        List<ServerPerformanceMetrics> list = new ArrayList<>();
        try {
            for (var settingsOfTesting : settingsOfComplexTesting) {
                list.add(testServerPerformance(settingsOfTesting));
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return AggregateServerPerformanceMetrics.create(list);
    }

    private ServerPerformanceMetrics testServerPerformance(SettingsOfServerPerformanceTesting settings) throws IOException, ExecutionException, InterruptedException {
        runServer(settings);
        //FIXME. Magic const
        Thread.sleep(1000);
        ClientMetrics clientMetrics = runClients(settings);
        ServerMetrics serverMetrics = getManagingServerResponse();
        return ServerPerformanceMetrics.create(serverMetrics.getRequestProcessingTime(), serverMetrics.getClientProcessingTime(), clientMetrics.getAverageTimeSpendByClient());
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
            throws InterruptedException, ExecutionException, IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(settings.getNumberOfClients());
        CompletionService<ClientMetrics> completionService = new ExecutorCompletionService<>(executorService);
        ClientSettings clientSettings = ClientSettings.create(settings.getServerType(),
                settings.getSizeOfRequest(),
                settings.getNumberOfRequestPerClient(),
                settings.getClientSleepTime());

        for (int index = 0; index < settings.getNumberOfClients(); index++) {
            Client client = Client.create(clientSettings);
            completionService.submit(client);
        }
        Future<ClientMetrics> future = completionService.take();
        ClientMetrics firstClientMetrics = future.get();
        haltServer();

        List<ClientMetrics> clientMetricsList = new ArrayList<>(
                Collections.singletonList(firstClientMetrics)
        );
        for (int index = 0; index < settings.getNumberOfClients() - 1; index++) {
            future = completionService.take();
            ClientMetrics clientMetrics = future.get();
            clientMetricsList.add(clientMetrics);
        }
        OptionalDouble average = clientMetricsList.stream().mapToDouble(ClientMetrics::getAverageTimeSpendByClient).average();
        return ClientMetrics.create(average.orElseThrow());
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
