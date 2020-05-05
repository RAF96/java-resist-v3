package ru.ifmo.java.managingServer;

//TODO
public interface ManagingServer extends Runnable {
    static void main(String[] args) {
        new ManagingServerImpl().run();
    }
}
