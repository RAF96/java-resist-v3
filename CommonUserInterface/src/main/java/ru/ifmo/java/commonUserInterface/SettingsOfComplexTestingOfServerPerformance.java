package ru.ifmo.java.commonUserInterface;

import ru.ifmo.java.common.ServerType;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public interface SettingsOfComplexTestingOfServerPerformance extends Iterable<SettingsOfServerPerformanceTesting> {
    static SettingsOfComplexTestingOfServerPerformance create(
            List<Integer> rangeOfVariableParameter,
            TypeOfVariableParameter typeOfVariableParameter,
            ServerType serverType,
            int numberOfClients,
            int sizeOfRequest,
            int numberOfRequestPerClient,
            int clientSleepTime
    ) {
        return new SettingsOfComplexTestingOfServerPerformance() {
            @Override
            @Nonnull
            public Iterator<SettingsOfServerPerformanceTesting> iterator() {
                return new Iterator<>() {
                    int rangeIndex = 0;
                    int sizeOfRequest;
                    int numberOfClients;
                    int clientSleepTime;

                    {
                        this.sizeOfRequest = sizeOfRequest;
                        this.numberOfClients = numberOfClients;
                        this.clientSleepTime = clientSleepTime;
                    }

                    @Override
                    public boolean hasNext() {
                        return rangeIndex == rangeOfVariableParameter.size();
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
        };
    }

}
