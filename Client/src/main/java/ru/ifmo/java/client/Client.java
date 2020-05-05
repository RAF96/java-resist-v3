package ru.ifmo.java.client;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

//TODO
public interface Client extends Callable<ClientMetrics> {
    static Client create(ClientSettings clientSettings) {
        return null;
    }
}
