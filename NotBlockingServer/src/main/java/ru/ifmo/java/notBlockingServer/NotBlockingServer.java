package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NotBlockingServer implements ComputeServer {
    final private ComputeServerSettings computeServerSettings;
    private ServerMetrics serverMetrics;

    public NotBlockingServer(ComputeServerSettings computeServerSettings) {
        this.computeServerSettings = computeServerSettings;
    }

    @Override
    public ServerMetrics getServerMetrics() {
        return serverMetrics;
    }

    @Override
    public void run() {
        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector readerSelector = Selector.open();
            Selector writerSelector = Selector.open()
        ) {
            serverSocketChannel.bind(new InetSocketAddress(Constant.computeServerPort));
            for (int index = 0; index < computeServerSettings.getNumberOfClients(); index++) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(readerSelector, SelectionKey.OP_READ);
            }

            TaskOfWritingAllRequests taskOfWritingAllRequests = TaskOfWritingAllRequests.create(writerSelector);
            ExecutorService workerThreadPool = Executors.newFixedThreadPool(Constant.numberOfThreads);
            TaskOfReadingAllRequests taskOfReadingAllRequests = TaskOfReadingAllRequests.create(
                    readerSelector,
                    workerThreadPool,
                    taskOfWritingAllRequests);

            Thread writerThread = new Thread(taskOfWritingAllRequests, "writerThread");
            writerThread.start();

            try {
                taskOfReadingAllRequests.run();
            } finally {
                workerThreadPool.shutdownNow();
                writerThread.interrupt();
                writerThread.join();
            }
            serverMetrics = taskOfWritingAllRequests.getMetrics();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
