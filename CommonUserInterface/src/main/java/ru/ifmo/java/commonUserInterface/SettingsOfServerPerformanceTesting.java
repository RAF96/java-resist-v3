package ru.ifmo.java.commonUserInterface;

import ru.ifmo.java.common.ServerType;

public interface SettingsOfServerPerformanceTesting {
    static SettingsOfServerPerformanceTesting create(
            ServerType serverType,
            int numberOfClients,
            int sizeOfRequest,
            int numberOfRequestPerClient,
            int clientSleepTime
    ) {
        return new SettingsOfServerPerformanceTesting() {
            @Override
            public ServerType getServerType() {
                return serverType;
            }

            @Override
            public int getNumberOfClients() {
                return numberOfClients;
            }

            @Override
            public int getSizeOfRequest() {
                return sizeOfRequest;
            }

            @Override
            public int getNumberOfRequestPerClient() {
                return numberOfRequestPerClient;
            }

            @Override
            public int getClientSleepTime() {
                return clientSleepTime;
            }

        };
    }


    ServerType getServerType();

    int getNumberOfClients();

    int getSizeOfRequest();

    int getNumberOfRequestPerClient();

    int getClientSleepTime();
}
