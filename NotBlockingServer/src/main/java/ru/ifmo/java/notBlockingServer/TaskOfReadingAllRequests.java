package ru.ifmo.java.notBlockingServer;

import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;


public interface TaskOfReadingAllRequests extends Runnable {
    static TaskOfReadingAllRequestsImpl create(Selector readerSelector,
                                               ExecutorService workerThreadPool,
                                               TaskOfWritingAllRequests taskOfWritingAllRequests) {
        return new TaskOfReadingAllRequestsImpl(readerSelector, workerThreadPool, taskOfWritingAllRequests);
    }


}
