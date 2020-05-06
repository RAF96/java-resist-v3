package ru.ifmo.java.computeServer;

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
}
