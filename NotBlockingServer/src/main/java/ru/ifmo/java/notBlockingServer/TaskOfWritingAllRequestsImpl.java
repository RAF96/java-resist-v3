package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TaskOfWritingAllRequestsImpl implements TaskOfWritingAllRequests {
    private final Selector selector;
    private final ConcurrentMap<SocketChannel, Pair> newRegisterSocketChannels = new ConcurrentHashMap<>();
    private final Map<SelectionKey, ByteBuffer> mapSelectionKeyToMessage = new HashMap<>();
    private final List<AverageServerMetrics> serverMetricsList = new ArrayList<>();
    private ServerMetrics serverMetrics;

    private static class Pair {
        private final ServerMetrics4 serverMetrics4;
        private final List<Double> list;

        Pair(ServerMetrics4 serverMetrics4, List<Double> list) {
            this.serverMetrics4 = serverMetrics4;
            this.list = list;
        }

    }

    public TaskOfWritingAllRequestsImpl(Selector selector) {
        this.selector = selector;
    }

    @Override
    public ServerMetrics getMetrics() {
        return serverMetrics;
    }

    @Override
    public void registerChannel(SocketChannel socketChannel, ServerMetrics4 serverMetrics4, List<Double> list) {
       newRegisterSocketChannels.putIfAbsent(socketChannel, new Pair(serverMetrics4, list));
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                registerNewChannels();
                processingReadyChannels();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serverMetrics = AverageServerMetrics.average(serverMetricsList);
        }
    }

    private void registerNewChannels() throws ClosedChannelException {
        Iterator<Map.Entry<SocketChannel, Pair>> iterator = newRegisterSocketChannels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SocketChannel, Pair> pairEntry = iterator.next();
            SocketChannel socketChannel = pairEntry.getKey();
            List<Double> list = pairEntry.getValue().list;
            ServerMetrics4 serverMetrics4 = pairEntry.getValue().serverMetrics4;
            serverMetrics4.setRequestProcessingEnd(System.currentTimeMillis());
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_WRITE);
            serverMetricsList.add(AverageServerMetrics.create(serverMetrics4.getRequestProcessingTime(),
                    serverMetrics4.getClientProcessingTime(),
                    1));
            ByteBuffer message = ByteBuffer.wrap(
                    MessageProcessing.packMessage(
                            MessageWithListOfDoubleVariables.newBuilder().addAllNumber(list).build().toByteArray()));
            message.flip();
            mapSelectionKeyToMessage.put(selectionKey, message);
            iterator.remove();
        }
    }

    private void processingReadyChannels() throws IOException {
        int select = selector.select();
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = mapSelectionKeyToMessage.get(key);
            socketChannel.write(buffer);
            if (buffer.position() == buffer.limit())  {
                clear(key);
            }
            iterator.remove();
        }
    }

    private void clear(SelectionKey key) {
        key.cancel();
        mapSelectionKeyToMessage.remove(key);
    }
}
