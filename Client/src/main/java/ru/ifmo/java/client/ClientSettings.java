package ru.ifmo.java.client;

import ru.ifmo.java.common.ServerType;

public interface ClientSettings {
    static ClientSettings create(int sizeOfRequest,
                                 int numberOfRequest,
                                 int clientSleepTime) {
        return new ClientSettings() {

            @Override
            public int getSizeOfRequest() {
                return sizeOfRequest;
            }

            @Override
            public int getNumberOfRequest() {
                return numberOfRequest;
            }

            @Override
            public int getClientSleepTime() {
                return clientSleepTime;
            }
        };
    }

    int getSizeOfRequest();

    int getNumberOfRequest();

    int getClientSleepTime();
}
