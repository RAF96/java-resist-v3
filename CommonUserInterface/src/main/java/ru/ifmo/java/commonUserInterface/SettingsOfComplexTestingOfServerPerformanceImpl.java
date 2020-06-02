package ru.ifmo.java.commonUserInterface;

import ru.ifmo.java.common.ServerType;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SettingsOfComplexTestingOfServerPerformanceImpl implements SettingsOfComplexTestingOfServerPerformance {
    private final List<Integer> rangeOfVariableParameter;
    private final TypeOfVariableParameter typeOfVariableParameter;
    private final ServerType serverType;
    private int sizeOfRequest;
    private final int numberOfRequestPerClient;
    private int numberOfClients;
    private int clientSleepTime;

    public SettingsOfComplexTestingOfServerPerformanceImpl(List<Integer> rangeOfVariableParameter,
                                                           TypeOfVariableParameter typeOfVariableParameter,
                                                           ServerType serverType,
                                                           int numberOfClients,
                                                           int sizeOfRequest,
                                                           int numberOfRequestPerClient,
                                                           int clientSleepTime) {
        this.rangeOfVariableParameter = rangeOfVariableParameter;
        this.typeOfVariableParameter = typeOfVariableParameter;
        this.serverType = serverType;
        this.numberOfClients = numberOfClients;
        this.sizeOfRequest = sizeOfRequest;
        this.numberOfRequestPerClient = numberOfRequestPerClient;
        this.clientSleepTime = clientSleepTime;
    }

    @Override
    @Nonnull
    public Iterator<SettingsOfServerPerformanceTesting> iterator() {
        return new Iterator<>() {
            int rangeIndex = 0;

            @Override
            public boolean hasNext() {
                return rangeIndex != rangeOfVariableParameter.size();
            }

            @Override
            public SettingsOfServerPerformanceTesting next() {
                foo();
                SettingsOfServerPerformanceTesting settings = SettingsOfServerPerformanceTesting.create(
                        serverType,
                        numberOfClients,
                        sizeOfRequest,
                        numberOfRequestPerClient,
                        clientSleepTime
                );
                rangeIndex += 1;
                return settings;
            }

            private void foo() {
                switch (typeOfVariableParameter) {
                    case SIZE_OF_REQUEST:
                        sizeOfRequest = rangeOfVariableParameter.get(rangeIndex);
                    case CLIENT_SLEEP_TIME:
                        clientSleepTime = rangeOfVariableParameter.get(rangeIndex);
                        break;
                    case NUMBER_OF_CLIENTS:
                        numberOfClients = rangeOfVariableParameter.get(rangeIndex);
                        break;
                    default:
                        throw new TypeOfVariableParameter.UnknownTypeOfVariableParameter();
                }
            }


        };
    }

    public List<Integer> getRangeOfVariableParameter() {
        return rangeOfVariableParameter;
    }

    public TypeOfVariableParameter getTypeOfVariableParameter() {
        return typeOfVariableParameter;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public int getSizeOfRequest() {
        return sizeOfRequest;
    }

    public int getNumberOfRequestPerClient() {
        return numberOfRequestPerClient;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getClientSleepTime() {
        return clientSleepTime;
    }

    @Override
    public String toString() {
        String listStr = rangeOfVariableParameter.stream().map(Objects::toString).collect(Collectors.joining(", "));
        return
                "rangeOfVariableParameter" + "\n" +
                        listStr + "\n" +
                        "typeOfVariableParameter" + "\n" +
                        typeOfVariableParameter.toString() + "\n" +
                        "serverType" + "\n" +
                        serverType.toString() + "\n" +
                        "sizeOfRequest" + "\n" +
                        sizeOfRequest + "\n" +
                        "numberOfRequestPerClient" + "\n" +
                        numberOfRequestPerClient + "\n" +
                        "numberOfCLients" + "\n" +
                        numberOfClients + "\n" +
                        "clientSleepTime" + "\n" +
                        clientSleepTime;
    }
}
