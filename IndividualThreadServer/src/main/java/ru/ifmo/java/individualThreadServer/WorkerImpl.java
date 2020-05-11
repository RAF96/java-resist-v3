package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class WorkerImpl implements Worker {
    private final Socket socket;
    private final CountDownLatch latch;
    private final ComputeServerSettings computeServerSettings;

    public WorkerImpl(Socket socket, CountDownLatch latch, ComputeServerSettings computeServerSettings) {
        this.socket = socket;
        this.latch = latch;
        this.computeServerSettings = computeServerSettings;
    }

    @Override
    public List<ServerMetrics> call() throws Exception {
        latch.countDown();
        latch.await();
        List<ServerMetrics> serverMetricsList = new ArrayList<>();
        while (!Thread.interrupted() && !socket.isClosed()) {
            byte[] bytes = MessageProcessing.readPackedMessage(socket.getInputStream());
            List<Double> numberList = MessageWithListOfDoubleVariables.parseFrom(bytes).getNumberList();
            Function<ServerMetrics4, ServerMetrics4> serverMetrics4Initializer = (ServerMetrics4 x) -> {
                x.setClientProcessingStart(System.currentTimeMillis());
                x.setRequestProcessingStart(System.currentTimeMillis());
                return x;
            };
            List<Double> list = Sort.sort(numberList);
            serverMetrics4Initializer = serverMetrics4Initializer.andThen((ServerMetrics4 x) ->
            {
                x.setRequestProcessingEnd(System.currentTimeMillis());
                x.setClientProcessingEnd(System.currentTimeMillis());
                return x;
            });
            serverMetricsList.add(serverMetrics4Initializer.apply(ServerMetrics4.create()));
        }
        return serverMetricsList;
    }
}
