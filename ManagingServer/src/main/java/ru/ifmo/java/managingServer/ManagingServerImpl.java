package ru.ifmo.java.managingServer;

import ru.ifmo.java.common.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ManagingServerImpl implements ManagingServer {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Constant.managingServerPort)) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<?> future = executorService.submit(ManagingServerSocketLoop.create(serverSocket));
            //FIXME. is it correct catching of exception?
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
