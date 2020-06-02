package ru.ifmo.java.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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

import static javafx.collections.FXCollections.observableArrayList;

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

    @FXML
    private LineChart<Integer, Double> requestProcessingTime;
    @FXML
    private LineChart<Integer, Double> clientProcessingTime;
    @FXML
    private LineChart<Integer, Double> averageTimeSpendByClient;

    public Controller() {
        try {
            commonUserInterface = CommonUserInterface.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static private ObservableList<XYChart.Series<Integer, Double>> mapData2Chart(List<Integer> x, List<Double> y) {
        assert x.size() == y.size();
        int size = x.size();
        ObservableList<XYChart.Data<Integer, Double>> list = observableArrayList();
        for (int index = 0; index < size; index++) {
            list.add(new XYChart.Data<>(x.get(index), y.get(index)));
        }
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();
        series.setData(list);
        series.setName("data");
        return FXCollections.observableArrayList(series);
    }

    @FXML
    private void initialize() {
        typeOfVariableParameterChoiceBox.setItems(observableArrayList(TypeOfVariableParameter.values()));
        typeOfServerChoiceBox.setItems(observableArrayList(ServerType.values()));
    }

    @FXML
    public void run() throws IOException, InterruptedException {
        boolean successful = false;
        try {
            SettingsOfComplexTestingOfServerPerformance settings = createSettings();
            AggregateServerPerformanceMetrics aggregateServerPerformanceMetrics =
                    commonUserInterface.runComplexTestingOfServerPerformance(settings);
            assert aggregateServerPerformanceMetrics.getClientProcessingTime() != null;
            assert aggregateServerPerformanceMetrics.getClientProcessingTime().size() != 0;
            assert aggregateServerPerformanceMetrics.getRequestProcessingTime() != null;
            assert aggregateServerPerformanceMetrics.getRequestProcessingTime().size() != 0;
            assert aggregateServerPerformanceMetrics.getAverageTimeSpendByClient() != null;
            assert aggregateServerPerformanceMetrics.getAverageTimeSpendByClient().size() != 0;
            show(settings, aggregateServerPerformanceMetrics);
            successful = true;
        } finally {
            if (successful) {
                status.setText("Success");
            } else {
                status.setText("Failure");
            }
        }
    }


    private void show(SettingsOfComplexTestingOfServerPerformance settings,
                      AggregateServerPerformanceMetrics metrics) {
        List<Integer> x = settings.getRangeOfVariableParameter();
        requestProcessingTime.setData(mapData2Chart(x, metrics.getRequestProcessingTime()));
        clientProcessingTime.setData(mapData2Chart(x, metrics.getClientProcessingTime()));
        averageTimeSpendByClient.setData(mapData2Chart(x, metrics.getAverageTimeSpendByClient()));
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
        int numberOfClients = Integer.parseInt(numberOfClientsTextField.getCharacters().toString());
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
