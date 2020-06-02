package ru.ifmo.java.commonUserInterface;

import ru.ifmo.java.client.Client;
import ru.ifmo.java.client.ClientMetrics;
import ru.ifmo.java.client.ClientSettings;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.common.protocol.Protocol.MetricsOfComputingServer;
import ru.ifmo.java.common.protocol.Protocol.RequestOfComputingServer;
import ru.ifmo.java.common.protocol.Protocol.RequestOfComputingServerStartup;
import ru.ifmo.java.common.protocol.Protocol.RequestOfHaltingOfComputingServer;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
        AggregateServerPerformanceMetrics aggregateServerPerformanceMetrics = AggregateServerPerformanceMetrics.create(list);
        saveComplexTesting(aggregateServerPerformanceMetrics, settingsOfComplexTesting);
        return aggregateServerPerformanceMetrics;
    }

    private void save(String string, String pathToFile) throws IOException {
        File file = new File(pathToFile);
        File parent = new File(file.getParent());
        if (!parent.exists()) {
            boolean flag = parent.mkdirs();
            assert !flag : "Problems with creating folder";
        }
        if (!file.exists()) {
            boolean flag = file.createNewFile();
            assert !flag : "Problems with creating file";
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(string.getBytes());
    }

    private void saveComplexTesting(AggregateServerPerformanceMetrics aggregateServerPerformanceMetrics,
                                    SettingsOfComplexTestingOfServerPerformance settingsOfComplexTesting) throws IOException {

        String requestProcessingPath = Path.of(Constant.pathToFolderWithMetricsOfLastRunning, "requestProcessing").toString();
        String requestProcessingStr = aggregateServerPerformanceMetrics.getRequestProcessingTime().stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        save(requestProcessingStr, requestProcessingPath);

        String clientProcessingPath = Path.of(Constant.pathToFolderWithMetricsOfLastRunning, "clientProcessing").toString();
        String clientProcessingStr = aggregateServerPerformanceMetrics.getClientProcessingTime().stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        save(clientProcessingStr, clientProcessingPath);

        String averageTimeSpendByClientPath = Path.of(Constant.pathToFolderWithMetricsOfLastRunning, "averageTimeSpendByClient").toString();
        String averageTimeSpendByClientStr = aggregateServerPerformanceMetrics.getAverageTimeSpendByClient().stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        save(averageTimeSpendByClientStr, averageTimeSpendByClientPath);


        String settingsPath = Path.of(Constant.pathToFolderWithMetricsOfLastRunning, "settings").toString();
        String settingsStr = settingsOfComplexTesting.toString();
        save(settingsStr, settingsPath);
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
        }
        haltServer();

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

    @Override
    public void clear() {
        try {
            managingServerSocket.close();
        } catch (IOException ignored) {
        }
    }

}
