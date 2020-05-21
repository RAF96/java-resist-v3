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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class WorkerImpl implements Worker {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final CountDownLatch latch;
    private final ComputeServerSettings computeServerSettings;
    private volatile AverageServerMetrics averageServerMetrics;

    public WorkerImpl(Socket socket, CountDownLatch latch, ComputeServerSettings computeServerSettings) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        this.latch = latch;
        this.computeServerSettings = computeServerSettings;
    }

    @Override
    public void run() {
        try {
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
                    bytes = MessageProcessing.readPackedMessage(inputStream);
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
                ServerMetrics4 serverMetrics4 = ServerMetrics4.create();
                serverMetrics4.setClientProcessingStart(System.currentTimeMillis());
                serverMetrics4.setRequestProcessingStart(System.currentTimeMillis());
                List<Double> list = Sort.sort(numberList);
                serverMetrics4.setRequestProcessingEnd(System.currentTimeMillis());
                serverMetrics4.setClientProcessingEnd(System.currentTimeMillis());
                byte[] response = MessageWithListOfDoubleVariables.newBuilder().addAllNumber(list).build().toByteArray();
                try {
                    outputStream.write(MessageProcessing.packMessage(response));
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                serverMetricsList.add(serverMetrics4);
            }
            averageServerMetrics = AverageServerMetrics.create(
                    serverMetricsList.stream().mapToDouble(ServerMetrics::getRequestProcessingTime).sum(),
                    serverMetricsList.stream().mapToDouble(ServerMetrics::getClientProcessingTime).sum(),
                    index);
            int x = 1;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public AverageServerMetrics getAverageServerMetrics() {
        return averageServerMetrics;
    }
}
