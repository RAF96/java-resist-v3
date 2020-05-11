package ru.ifmo.java.commonPartsOfComputeServer;

import java.util.function.Function;

public interface ServerMetrics4 extends ServerMetrics{
    static ServerMetrics4 create() {
        return new ServerMetrics4() {
            private double requestProcessingStart;
            private double requestProcessingEnd;
            private double clientProcessingStart;
            private double clientProcessingEnd;

            public double getRequestProcessingStart() {
                return requestProcessingStart;
            }

            public void setRequestProcessingStart(double requestProcessingStart) {
                this.requestProcessingStart = requestProcessingStart;
            }

            public double getRequestProcessingEnd() {
                return requestProcessingEnd;
            }

            public void setRequestProcessingEnd(double requestProcessingEnd) {
                this.requestProcessingEnd = requestProcessingEnd;
            }

            public double getClientProcessingStart() {
                return clientProcessingStart;
            }

            public void setClientProcessingStart(double clientProcessingStart) {
                this.clientProcessingStart = clientProcessingStart;
            }

            public double getClientProcessingEnd() {
                return clientProcessingEnd;
            }

            public void setClientProcessingEnd(double clientProcessingEnd) {
                this.clientProcessingEnd = clientProcessingEnd;
            }

            @Override
            public double getRequestProcessingTime() {
                return getRequestProcessingEnd() - getRequestProcessingStart();
            }

            @Override
            public double getClientProcessingTime() {
                return getClientProcessingEnd() - getClientProcessingStart();
            }
        };
    }
    double getRequestProcessingStart() ;

    void setRequestProcessingStart(double requestProcessingStart) ;

    double getRequestProcessingEnd() ;

    void setRequestProcessingEnd(double requestProcessingEnd) ;

    double getClientProcessingStart() ;

    void setClientProcessingStart(double clientProcessingStart) ;

    double getClientProcessingEnd() ;

    void setClientProcessingEnd(double clientProcessingEnd) ;
}
