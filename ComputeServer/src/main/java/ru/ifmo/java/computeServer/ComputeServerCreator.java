package ru.ifmo.java.computeServer;

import ru.ifmo.java.blockingServer.BlockingServerImpl;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServer;
import ru.ifmo.java.commonPartsOfComputeServer.ComputeServerSettings;
import ru.ifmo.java.commonPartsOfComputeServer.ServerMetrics;
import ru.ifmo.java.individualThreadServer.IndividualThreadServer;
import ru.ifmo.java.notBlockingServer.NotBlockingServer;

public interface ComputeServerCreator {
    static ComputeServer newIndividualThreadServer(ComputeServerSettings computeServerSettings) {
        return new IndividualThreadServer(computeServerSettings);
    }

    static ComputeServer newBlockingServer(ComputeServerSettings computeServerSettings) {
        return new BlockingServerImpl(computeServerSettings);
    }

    static ComputeServer newNotBlockingServer(ComputeServerSettings computeServerSettings) {
        return new NotBlockingServer(computeServerSettings);
    }

    static ComputeServer newComputeServer(ServerType serverType, ComputeServerSettings computeServerSettings) {
        switch (serverType) {
            case INDIVIDUAL_THREAD_SERVER:
                return newIndividualThreadServer(computeServerSettings);
            case BLOCKING_THREAD_SERVER:
                return newBlockingServer(computeServerSettings);
            case NOT_BLOCKING_THREAD_SERVER:
                return newNotBlockingServer(computeServerSettings);
            default:
                throw new ServerType.UnknownServerType();
        }
    }
}
