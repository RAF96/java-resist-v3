package ru.ifmo.java.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.ifmo.java.common.ServerType;
import ru.ifmo.java.commonUserInterface.AggregateServerPerformanceMetrics;
import ru.ifmo.java.commonUserInterface.CommonUserInterface;
import ru.ifmo.java.commonUserInterface.SettingsOfComplexTestingOfServerPerformance;
import ru.ifmo.java.commonUserInterface.TypeOfVariableParameter;

import java.io.IOException;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller {
    private final CommonUserInterface commonUserInterface;
    @FXML
    private ChoiceBox<TypeOfVariableParameter> typeOfVariableParameterChoiceBox;
    @FXML
    private Button runningButton;
    @FXML
    private ChoiceBox<ServerType> typeOfServerChoiceBox;
    @FXML
    private TextField startOfVariableParameter;
    @FXML
    private TextField endOfVariableParameter;
    @FXML
    private TextField numberOfClientsTextField;
    @FXML
    private TextField numberOfRequestsByClient;
    @FXML
    private TextField sleepTimeTextField;
    @FXML
    private TextField deltaOfVariableParameter;
    @FXML
    private Label status;
    @FXML
    private TextField requestSizeTextField;

    public Controller() {
        try {
            commonUserInterface = CommonUserInterface.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        typeOfVariableParameterChoiceBox.setItems(FXCollections.observableArrayList(TypeOfVariableParameter.values()));
        typeOfServerChoiceBox.setItems(FXCollections.observableArrayList(ServerType.values()));
    }

    @FXML
    public void run() throws IOException, InterruptedException {
        try {
            SettingsOfComplexTestingOfServerPerformance settings = createSettings();
            AggregateServerPerformanceMetrics aggregateServerPerformanceMetrics =
                    commonUserInterface.runComplexTestingOfServerPerformance(settings);
            uploadMetrics(settings, aggregateServerPerformanceMetrics);
            show(settings, aggregateServerPerformanceMetrics);
        } finally {
            status.setText("Failure");
        }
        status.setText("Success");
    }

    //TODO
    private void show(SettingsOfComplexTestingOfServerPerformance settings,
                      AggregateServerPerformanceMetrics aggregateServerPerformanceMetrics) {

    }

    //TODO
    private void uploadMetrics(SettingsOfComplexTestingOfServerPerformance settingsOfServerPerformanceTestings,
                               AggregateServerPerformanceMetrics aggregateServerPerformanceMetrics) {
    }

    private SettingsOfComplexTestingOfServerPerformance createSettings() {
        int start = Integer.parseInt(startOfVariableParameter.getCharacters().toString());
        int end = Integer.parseInt(endOfVariableParameter.getCharacters().toString());
        int delta = Integer.parseInt(deltaOfVariableParameter.getCharacters().toString());
        assert start < end : String.format("start: %d, end %d", start, end);
        assert delta > 0 : String.format("delta: %d", delta);
        List<Integer> rangeOfVariableParameter = IntStream.generate(new IntSupplier() {
            int i = 0;

            @Override
            public int getAsInt() {
                int res = start + i * delta;
                i += 1;
                assert res < end;
                return res;
            }
        }).limit((end - start + delta - 1) / delta).boxed().collect(Collectors.toList());

        TypeOfVariableParameter typeOfVariableParameter = typeOfVariableParameterChoiceBox.getValue();
        ServerType serverType = typeOfServerChoiceBox.getValue();
        int numberOfClients = Integer.parseInt(numberOfRequestsByClient.getCharacters().toString());
        int sizeOfRequest = Integer.parseInt(requestSizeTextField.getCharacters().toString());
        int numberOfRequestPerClient = Integer.parseInt(numberOfRequestsByClient.getCharacters().toString());
        int clientSleepTime = Integer.parseInt(sleepTimeTextField.getCharacters().toString());
        return SettingsOfComplexTestingOfServerPerformance.create(rangeOfVariableParameter,
                typeOfVariableParameter,
                serverType,
                numberOfClients,
                sizeOfRequest,
                numberOfRequestPerClient,
                clientSleepTime);
    }
}
