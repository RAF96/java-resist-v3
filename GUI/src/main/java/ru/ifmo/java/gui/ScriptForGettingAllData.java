package ru.ifmo.java.gui;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonUserInterface.CommonUserInterface;
import ru.ifmo.java.commonUserInterface.SettingsOfComplexTestingOfServerPerformance;
import ru.ifmo.java.commonUserInterface.TypeOfVariableParameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ScriptForGettingAllData {

    private static class ScriptSettings implements Iterable<SettingsOfComplexTestingOfServerPerformance> {

        private final List<List<Integer>> ranges = new ArrayList<>();
        private final int numberOfClients;
        private final int sizeOfRequest;
        private final int numberOfRequestPerClient;
        private final int clientSleepTime;

        public ScriptSettings(
                List<Integer> rangeForSizeOfRequest,
                List<Integer> rangeForNumberOfClients,
                List<Integer> rangeForSleepTime,
                int numberOfClients,
                int sizeOfRequest,
                int numberOfRequestPerClient,
                int clientSleepTime
                ) {
            this.numberOfClients = numberOfClients;
            this.sizeOfRequest = sizeOfRequest;
            this.numberOfRequestPerClient = numberOfRequestPerClient;
            this.clientSleepTime = clientSleepTime;
            ranges.add(rangeForSizeOfRequest);
            ranges.add(rangeForNumberOfClients);
            ranges.add(rangeForSleepTime);
        }

        @Override
        public Iterator<SettingsOfComplexTestingOfServerPerformance> iterator() {
            return new Iterator<>() {
                int typeOfServer = 0;
                int typeOfVariableParameter = 0;

                @Override
                public boolean hasNext() {
                    return typeOfServer < ServerType.values().length;
                }

                @Override
                public SettingsOfComplexTestingOfServerPerformance next() {
                    SettingsOfComplexTestingOfServerPerformance settings = SettingsOfComplexTestingOfServerPerformance.create(
                            ranges.get(typeOfVariableParameter),
                            TypeOfVariableParameter.values()[typeOfVariableParameter],
                            ServerType.values()[typeOfServer],
                            numberOfClients,
                            sizeOfRequest,
                            numberOfRequestPerClient,
                            clientSleepTime
                    );
                    if (typeOfVariableParameter == TypeOfVariableParameter.values().length) {
                        typeOfVariableParameter = 0;
                        typeOfServer += 1;
                    }
                    return settings;
                }
            };
        }
    }

    public static void main(String ... args) throws IOException, InterruptedException {
        CommonUserInterface commonUserInterface = CommonUserInterface.create();
        //FIXME. should be more represented parameters
        ScriptSettings scriptSettings = new ScriptSettings(
                Arrays.asList(1, 3, 7, 10),
                Arrays.asList(1, 3, 7, 10),
                Arrays.asList(1, 3, 7, 10),
                10,
                10,
                10,
                10
        );
        int number = 0;
        new File(Constant.pathToScriptData).delete();
        for (var settings : scriptSettings) {
            commonUserInterface.runComplexTestingOfServerPerformance(settings);
            File from = new File(Constant.pathToFolderWithMetricsOfLastRunning);
            File to = Path.of(Constant.pathToScriptData, String.valueOf(number++)).toFile();
            to.mkdirs();
            for (var file : from.listFiles()) {
                Files.copy(file.toPath(), Path.of(to.getPath(), file.getName()));
            }
        }

    }


}
