package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.nio.channels.SocketChannel;
import java.util.List;

public class WorkerImpl implements Worker {
    private final ServerMetrics4 serverMetrics4;
    private final TaskOfWritingAllRequests taskOfWritingAllRequests;
    private final List<Double> numberList;
    private final SocketChannel socketChannel;

    public WorkerImpl(ServerMetrics4 serverMetrics4,
                      TaskOfWritingAllRequests taskOfWritingAllRequests,
                      List<Double> numberList,
                      SocketChannel socketChannel) {
        this.serverMetrics4 = serverMetrics4;
        this.taskOfWritingAllRequests = taskOfWritingAllRequests;
        this.numberList = numberList;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        serverMetrics4.setRequestProcessingStart(System.currentTimeMillis());
        List<Double> list = Sort.sort(numberList);
        serverMetrics4.setRequestProcessingEnd(System.currentTimeMillis());
        taskOfWritingAllRequests.registerChannel(socketChannel, serverMetrics4, list);
    }
}
