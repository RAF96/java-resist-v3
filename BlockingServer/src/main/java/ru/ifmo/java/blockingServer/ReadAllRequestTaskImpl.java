package ru.ifmo.java.blockingServer;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.protocol.Protocol;
import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class ReadAllRequestTaskImpl implements ReadAllRequestTask {
    private final ComputeServerSettings settings;
    private final CountDownLatch countDownLatch;
    private final Socket socket;
    private final ExecutorService workersThread;
    private final ExecutorService writerTaskExecutor = Executors.newSingleThreadExecutor();
    private final InputStream inputStream;
    private AverageServerMetrics averageServerMetrics;
    private List<Worker> workers = new ArrayList<>();
    private List<Future<?>> futures = new ArrayList<>();

    public ReadAllRequestTaskImpl(ComputeServerSettings settings, CountDownLatch countDownLatch, Socket socket, ExecutorService workersThread) throws IOException {
        this.settings = settings;
        this.countDownLatch = countDownLatch;
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.workersThread = workersThread;
    }

    @Override
    public AverageServerMetrics averageServerMetrics() {
        return averageServerMetrics;
    }

    @Override
    public void run() {
        try {
            processing();
            postprocessing();
        } catch (IOException ignored) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    void close() {
        writerTaskExecutor.shutdownNow();
    }

    void processing() throws IOException {
        for (int index = 0;index < settings.getNumberOfRequest(); index++) {
            byte[] bytes;
            bytes = MessageProcessing.readPackedMessage(inputStream);
            ServerMetrics4 serverMetrics4 = ServerMetrics4.create();
            serverMetrics4.setClientProcessingStart(System.currentTimeMillis());
            List<Double> numberList;
            numberList = Protocol.MessageWithListOfDoubleVariables.parseFrom(bytes).getNumberList();
            Worker worker = Worker.create(serverMetrics4, numberList, workersThread, socket);
            workers.add(worker);
            Future<?> future = workersThread.submit(worker);
            futures.add(future);
        }
    }

    void postprocessing() throws InterruptedException {
        for (var future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                try {
                    future.get();
                } catch (ExecutionException executionException) {
                    throw new RuntimeException(executionException);
                }
            }
        }
        List<ServerMetrics> serverMetricsList = workers.stream().map(Worker::getServerMetrics).filter(Objects::nonNull).collect(Collectors.toList());
        averageServerMetrics = AverageServerMetrics.create(
                serverMetricsList.stream().mapToDouble(ServerMetrics::getRequestProcessingTime).sum(),
                serverMetricsList.stream().mapToDouble(ServerMetrics::getClientProcessingTime).sum(),
                serverMetricsList.size());
    }
}
