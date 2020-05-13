package ru.ifmo.java.individualThreadServer;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class WorkerImpl implements Worker {
    private final Socket socket;
    private final CountDownLatch latch;
    private final ComputeServerSettings computeServerSettings;
    private volatile AverageServerMetrics averageServerMetrics;

    public WorkerImpl(Socket socket, CountDownLatch latch, ComputeServerSettings computeServerSettings) {
        this.socket = socket;
        this.latch = latch;
        this.computeServerSettings = computeServerSettings;
    }

    @Override
    public void run() {
        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        List<ServerMetrics> serverMetricsList = new ArrayList<>();
        int index = 0;
        for (; index < computeServerSettings.getNumberOfRequest() &&
                !Thread.interrupted() && !socket.isClosed(); ++index) {
            byte[] bytes;
            try {
                bytes = MessageProcessing.readPackedMessage(socket.getInputStream());
            } catch (IOException e) {
                break;
            }
            List<Double> numberList;
            try {
                numberList = MessageWithListOfDoubleVariables.parseFrom(bytes).getNumberList();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                break;
            }
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
            byte[] response = MessageWithListOfDoubleVariables.newBuilder().addAllNumber(list).build().toByteArray();
            try {
                socket.getOutputStream().write(MessageProcessing.packMessage(response));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            serverMetricsList.add(serverMetrics4Initializer.apply(ServerMetrics4.create()));
        }
        averageServerMetrics = AverageServerMetrics.create(
                serverMetricsList.stream().mapToDouble(ServerMetrics::getRequestProcessingTime).sum(),
                serverMetricsList.stream().mapToDouble(ServerMetrics::getClientProcessingTime).sum(),
                index);
    }

    @Override
    public AverageServerMetrics getAverageServerMetrics() {
        return averageServerMetrics;
    }
}
