package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.protocol.Protocol.*;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics4;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;


public class TaskOfReadingAllRequestsImpl implements TaskOfReadingAllRequests {
    private final Selector readerSelector;
    private final ExecutorService workerThreadPool;
    private final TaskOfWritingAllRequests taskOfWritingAllRequests;
    private final Map<SelectionKey, ByteBuffer> mapKey2byteBufferOfSizeOfMessage = new HashMap<>();
    private final Map<SelectionKey, Integer> mapKey2SizeOfMessage = new HashMap<>();
    private final Map<SelectionKey, ByteBuffer> mapKey2byteBufferOfBodyOfMessage = new HashMap<>();


    public TaskOfReadingAllRequestsImpl(Selector readerSelector,
                                        ExecutorService workerThreadPool,
                                        TaskOfWritingAllRequests taskOfWritingAllRequests) {
        this.readerSelector = readerSelector;
        this.workerThreadPool = workerThreadPool;
        this.taskOfWritingAllRequests = taskOfWritingAllRequests;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                readerSelector.select();
                Iterator<SelectionKey> iterator = readerSelector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    if (!mapKey2SizeOfMessage.containsKey(selectionKey)) {
                        readMessageSize(selectionKey, socketChannel);
                    }
                    if (mapKey2SizeOfMessage.containsKey(selectionKey)) {
                        MessageWithListOfDoubleVariables messageWithListOfDoubleVariables = readMessageBody(selectionKey, socketChannel);
                        if (messageWithListOfDoubleVariables != null) {
                            submitToSort(messageWithListOfDoubleVariables, socketChannel);
                        }
                        clear(selectionKey);
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void submitToSort(MessageWithListOfDoubleVariables messageWithListOfDoubleVariables,
                              SocketChannel socketChannel) {
        ServerMetrics4 serverMetrics4 = ServerMetrics4.create();
        serverMetrics4.setClientProcessingStart(System.currentTimeMillis());
        workerThreadPool.submit(Worker.create(serverMetrics4,
                taskOfWritingAllRequests,
                messageWithListOfDoubleVariables.getNumberList(),
                socketChannel));
    }

    private void readMessageSize(SelectionKey selectionKey, SocketChannel socketChannel) throws IOException {
        if (!mapKey2byteBufferOfSizeOfMessage.containsKey(selectionKey)) {
            mapKey2byteBufferOfSizeOfMessage.put(selectionKey, ByteBuffer.allocate(4));
        }
        ByteBuffer byteBuffer = mapKey2byteBufferOfSizeOfMessage.get(selectionKey);
        socketChannel.read(byteBuffer);
        if (byteBuffer.position() == byteBuffer.limit()) {
            byteBuffer.flip();
            mapKey2SizeOfMessage.put(selectionKey, byteBuffer.getInt());
        }
    }

    private MessageWithListOfDoubleVariables readMessageBody(SelectionKey selectionKey, SocketChannel socketChannel) throws IOException {
        int size = mapKey2SizeOfMessage.get(selectionKey);
        if (!mapKey2byteBufferOfBodyOfMessage.containsKey(selectionKey)) {
            mapKey2byteBufferOfBodyOfMessage.put(selectionKey, ByteBuffer.allocate(size));
        }
        ByteBuffer byteBuffer = mapKey2byteBufferOfBodyOfMessage.get(selectionKey);
        socketChannel.read(byteBuffer);
        if (byteBuffer.position() == byteBuffer.limit()) {
            byteBuffer.flip();
            return MessageWithListOfDoubleVariables.parseFrom(byteBuffer.array());
        }
        return null;
    }


    private void clear(SelectionKey selectionKey) {
        mapKey2byteBufferOfSizeOfMessage.remove(selectionKey);
        mapKey2SizeOfMessage.remove(selectionKey);
        mapKey2byteBufferOfBodyOfMessage.remove(selectionKey);
    }
}
