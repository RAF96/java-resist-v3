package ru.ifmo.java.client;

import java.util.concurrent.atomic.AtomicReference;

//TODO
public interface Client {
    static Client create(ClientSettings clientSettings, AtomicReference<ClientMetrics> clientMetrics) {
        return null;
    }
}
