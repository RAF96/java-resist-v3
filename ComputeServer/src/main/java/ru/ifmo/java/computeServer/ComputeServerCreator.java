package ru.ifmo.java.computeServer;

import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.individualThreadServer.IndividualThreadServer;

//TODO
public interface ComputeServerCreator {
    static ComputeServer newIndividualThreadServer(int numberOfClients) {
        return new IndividualThreadServer(ComputeServerSettings.create(numberOfClients));
    }

    static ComputeServer newBlockingServer(int numberOfClients) {
        return null;
    }

    static ComputeServer newNotBlockingServer(int numberOfClients) {
        return null;
    }

    static ComputeServer newComputeServer(ServerType serverType, int numberOfClients) {
        switch (serverType) {
            case INDIVIDUAL_THREAD_SERVER:
                return newIndividualThreadServer(numberOfClients);
            case BLOCKING_THREAD_SERVER:
                return newBlockingServer(numberOfClients);
            case NOT_BLOCKING_THREAD_SERVER:
                return newNotBlockingServer(numberOfClients);
            default:
                throw new ServerType.UnknownServerType();
        }
    }
}
