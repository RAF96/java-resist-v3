package ru.java.ifmo.commonUserInterface;

import ru.ifmo.java.common.protocol.Protocol.*;

public enum ServerType {
    INDIVIDUAL_THREAD_SERVER,
    BLOCKING_THREAD_SERVER,
    NOT_BLOCKING_THREAD_SERVER;

    public static ServerType protocolServerType2ServerType
            (RequestOfComputingServerStartup.ServerType protocolServerType) {
        ServerType serverType;
        switch (protocolServerType) {
            case BLOCKING_THREAD_SERVER:
                serverType = BLOCKING_THREAD_SERVER;
                break;
            case INDIVIDUAL_THREAD_SERVER:
                serverType = INDIVIDUAL_THREAD_SERVER;
                break;
            case NOT_BLOCKING_THREAD_SERVER:
                serverType = NOT_BLOCKING_THREAD_SERVER;
                break;
            default:
                throw new UnknownServerType();
        }
        return serverType;
    }


    public static RequestOfComputingServerStartup.ServerType
            serverType2ProtocolServerType(ServerType serverType) {
        RequestOfComputingServerStartup.ServerType protocolServerType;
        switch (serverType) {
            case BLOCKING_THREAD_SERVER:
                protocolServerType = RequestOfComputingServerStartup.ServerType.BLOCKING_THREAD_SERVER;
                break;
            case INDIVIDUAL_THREAD_SERVER:
                protocolServerType = RequestOfComputingServerStartup.ServerType.INDIVIDUAL_THREAD_SERVER;
                break;
            case NOT_BLOCKING_THREAD_SERVER:
                protocolServerType = RequestOfComputingServerStartup.ServerType.NOT_BLOCKING_THREAD_SERVER;
                break;
            default:
                throw new UnknownServerType();
        }
        return protocolServerType;
    }


    public static class UnknownServerType extends RuntimeException {
    }
}
