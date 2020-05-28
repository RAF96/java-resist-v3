package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.nio.channels.SocketChannel;
import java.util.List;

public interface Worker extends Runnable {
    static Worker create(ServerMetrics4 serverMetrics4,
                         TaskOfWritingAllRequests taskOfWritingAllRequests,
                         List<Double> numberList,
                         SocketChannel socketChannel) {
        return new WorkerImpl(serverMetrics4, taskOfWritingAllRequests, numberList, socketChannel);
    }
}
