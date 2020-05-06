package ru.ifmo.java.client;

import java.util.concurrent.Callable;

public interface Client extends Callable<ClientMetrics> {
    static Client create(ClientSettings clientSettings) {
        return new ClientImpl(clientSettings);
    }
}
