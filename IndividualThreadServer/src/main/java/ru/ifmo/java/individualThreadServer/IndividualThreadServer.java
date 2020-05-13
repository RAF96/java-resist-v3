package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.commonPartsOfComputeServer.AverageServerMetrics;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class IndividualThreadServer implements ComputeServer {

    private final ComputeServerSettings serverSettings;
    private volatile AverageServerMetrics serverMetrics;

    public IndividualThreadServer(ComputeServerSettings serverSettings) {
        this.serverSettings = serverSettings;
    }

    @Override
    public void run() {
        CountDownLatch latch = new CountDownLatch(serverSettings.getNumberOfClients());
        List<Worker> workers = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(Constant.computeServerPort))
        {
            for (int index = 0; index < serverSettings.getNumberOfClients() &&
                    !Thread.interrupted(); index++) {
                Socket socket = serverSocket.accept();
                workers.add(Worker.create(socket, latch, serverSettings));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        List<Thread> threads = new ArrayList<>();
        for (Worker worker : workers) {
            Thread thread = new Thread(worker, "IndividualThreadServer-worker");
            thread.start();
            threads.add(thread);
        }

        List<AverageServerMetrics> serverMetricsList = new ArrayList<>();
        for (int index = 0; index < serverSettings.getNumberOfClients(); index++) {
            Thread thread = threads.get(index);
            Worker worker = workers.get(index);
            if (Thread.interrupted()) {
                thread.interrupt();
            }
            //FIXME. In this place, thread can became interrupted
            try {
                thread.join();
            } catch (InterruptedException ignored) {
                thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            serverMetricsList.add(worker.getAverageServerMetrics());
        }
        serverMetrics = AverageServerMetrics.average(serverMetricsList);
    }

    @Override
    public AverageServerMetrics getServerMetrics() {
        return serverMetrics;
    }
}
