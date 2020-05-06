package ru.ifmo.java.computeServer;

import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;

//TODO
public interface ComputeServerCreator {
    static ComputeServer newIndividualThreadServer() {
        return null;
    }

    static ComputeServer newBlockingServer() {
        return null;
    }


    static ComputeServer newNotBlockingServer() {
        return null;
    }

    static ComputeServer newComputeServer(ServerType serverType) {
        switch (serverType) {
            case INDIVIDUAL_THREAD_SERVER:
                return newIndividualThreadServer();
            case BLOCKING_THREAD_SERVER:
                return newBlockingServer();
            case NOT_BLOCKING_THREAD_SERVER:
                return newNotBlockingServer();
            default:
                throw new ServerType.UnknownServerType();
        }
    }
}
