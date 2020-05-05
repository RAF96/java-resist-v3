package ru.ifmo.java.client;

import ru.ifmo.java.common.ServerType;

public interface ClientSettings {
    static ClientSettings create(ServerType serverType,
                                 int sizeOfRequest,
                                 int numberOfRequest,
                                 int clientSleepTime) {
        return new ClientSettings() {
            @Override
            public ServerType getServerType() {
                return serverType;
            }

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

    ServerType getServerType();

    int getSizeOfRequest();

    int getNumberOfRequest();

    int getClientSleepTime();
}
