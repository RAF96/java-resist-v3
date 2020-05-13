package ru.ifmo.java.client;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.protocol.Protocol.*;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class ClientImpl implements Client {
    private final ClientSettings clientSettings;
    private final Random random = new Random();
    private Socket socket;

    public ClientImpl(ClientSettings clientSettings) {
        assert clientSettings.getNumberOfRequest() > 0;
        this.clientSettings = clientSettings;
    }

    @Override
    public ClientMetrics call() throws Exception {
        long startTimeMillis = System.currentTimeMillis();
        int numberOfSentRequest = 0;
        try (Socket socket = initSocket()) {
            for (;numberOfSentRequest < clientSettings.getNumberOfRequest(); numberOfSentRequest++) {
                processingOneRequest();
                Thread.sleep(clientSettings.getClientSleepTime());
            }
        }
        long currentTimeMillis = System.currentTimeMillis();
        return ClientMetrics.create(numberOfSentRequest, currentTimeMillis - startTimeMillis);
    }

    private Socket initSocket() throws InterruptedException {
        boolean isSocketReady = false;
        Socket socket = null;
        do {
            try {
                socket = new Socket(Constant.serverHost, Constant.computeServerPort);
                isSocketReady = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            //FIXME. Magic constant
            Thread.sleep(1000);

        } while(!isSocketReady);
        return socket;
    }

    private void processingOneRequest() throws IOException {
        List<Double> list = random.doubles(clientSettings.getSizeOfRequest()).boxed().collect(Collectors.toList());
        MessageWithListOfDoubleVariables message = MessageWithListOfDoubleVariables.newBuilder().addAllNumber(list).build();
        byte[] packMessage = MessageProcessing.packMessage(message.toByteArray());
        socket.getOutputStream().write(packMessage);
        // waitResponse
        byte[] responseInBytes = MessageProcessing.readPackedMessage(socket.getInputStream());
        MessageWithListOfDoubleVariables response = MessageWithListOfDoubleVariables.parseFrom(responseInBytes);
        Collections.sort(list);
        assert Arrays.equals(response.getNumberList().toArray(), list.toArray());
    }

}
