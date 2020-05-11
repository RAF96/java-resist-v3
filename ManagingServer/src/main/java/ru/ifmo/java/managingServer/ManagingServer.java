package ru.ifmo.java.managingServer;

public interface ManagingServer extends Runnable {
    static ManagingServer create() {
        return new ManagingServerImpl();
    }
    static void main(String[] args) {
        new ManagingServerImpl().run();
    }
}
