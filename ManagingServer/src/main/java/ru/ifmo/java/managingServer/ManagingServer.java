package ru.ifmo.java.managingServer;

public interface ManagingServer extends Runnable {
    static void main(String[] args) {
        new ManagingServerImpl().run();
    }
}
