package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;


public interface TaskOfWritingAllRequests extends Runnable {
    static TaskOfWritingAllRequests create(Selector writerSelector) {
        return new TaskOfWritingAllRequestsImpl(writerSelector);
    }

    ServerMetrics getMetrics();

    void registerChannel(SocketChannel socketChannel, ServerMetrics4 serverMetrics4, List<Double> list);
}
