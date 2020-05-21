package ru.ifmo.java.client;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.MessageProcessing;
import ru.ifmo.java.common.protocol.Protocol;
import ru.ifmo.java.common.protocol.Protocol.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class ClientImpl implements Client {
    private final ClientSettings clientSettings;
    private final Random random = new Random();
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientImpl(ClientSettings clientSettings) {
        assert (clientSettings.getNumberOfRequest() > 0) : "numberOfRequests per user <= 0";
        this.clientSettings = clientSettings;
    }

    @Override
    public ClientMetrics call() throws InterruptedException, IOException {
        long startTimeMillis = System.currentTimeMillis();
        int numberOfSentRequest = 0;
        socket = initSocket();
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            try {
                for (; numberOfSentRequest < clientSettings.getNumberOfRequest(); numberOfSentRequest++) {
                    processingOneRequest();
                    Thread.sleep(clientSettings.getClientSleepTime());
                }
            } catch (IOException ignored) {
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        long currentTimeMillis = System.currentTimeMillis();
        return ClientMetrics.create(numberOfSentRequest, (currentTimeMillis - startTimeMillis) * 1.0 / numberOfSentRequest);
    }

    private Socket initSocket() throws InterruptedException {
        boolean isSocketReady = false;
        Socket socket = null;
        do {
            try {
                socket = new Socket(Constant.serverHost, Constant.computeServerPort);
                isSocketReady = true;
            } catch (IOException ignored) {
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
        outputStream.write(packMessage);
        // waitResponse
        byte[] responseInBytes = MessageProcessing.readPackedMessage(inputStream);
        MessageWithListOfDoubleVariables response;
        try {
            response = MessageWithListOfDoubleVariables.parseFrom(responseInBytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        Collections.sort(list);
        assert (Arrays.equals(response.getNumberList().toArray(), list.toArray())) : "sort is wrong";
    }

}
