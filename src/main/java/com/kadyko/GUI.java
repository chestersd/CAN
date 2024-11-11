package com.kadyko;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {

    private CANMessages canMessages;

    @Override
    public void start(Stage primaryStage) {
        canMessages = new CANMessages();

        primaryStage.setTitle("CAN Engine Monitor");

        // Ползунки и флажки для каждого параметра
        Slider coolantTempSlider = new Slider(0, 130, 20);
        coolantTempSlider.setShowTickLabels(true);
        coolantTempSlider.setShowTickMarks(true);
        coolantTempSlider.setMajorTickUnit(10);
        coolantTempSlider.setBlockIncrement(1);

        CheckBox coolantTempCheckBox = new CheckBox("Вкл.");
        coolantTempCheckBox.setSelected(true);

        Slider engineSpeedSlider = new Slider(300, 3000, 300);
        engineSpeedSlider.setShowTickLabels(true);
        engineSpeedSlider.setShowTickMarks(true);
        engineSpeedSlider.setMajorTickUnit(500);
        engineSpeedSlider.setBlockIncrement(50);

        CheckBox engineSpeedCheckBox = new CheckBox("Вкл.");
        engineSpeedCheckBox.setSelected(true);

        Slider ambientAirTempSlider = new Slider(-10, 40, 20);
        ambientAirTempSlider.setShowTickLabels(true);
        ambientAirTempSlider.setShowTickMarks(true);
        ambientAirTempSlider.setMajorTickUnit(10);
        ambientAirTempSlider.setBlockIncrement(5);

        CheckBox ambientAirTempCheckBox = new CheckBox("Вкл.");
        ambientAirTempCheckBox.setSelected(true);

        Slider intakeManifoldTempSlider = new Slider(-10, 110, 0);
        intakeManifoldTempSlider.setShowTickLabels(true);
        intakeManifoldTempSlider.setShowTickMarks(true);
        intakeManifoldTempSlider.setMajorTickUnit(20);
        intakeManifoldTempSlider.setBlockIncrement(10);

        CheckBox intakeManifoldTempCheckBox = new CheckBox("Вкл.");
        intakeManifoldTempCheckBox.setSelected(true);

        // Кнопки запуска и остановки
        Button startButton = new Button("Start");
//        startButton.setFont(new Font(12));
        startButton.setOnAction(e -> {
            canMessages.startSending();
            updateCANParameters(
                    coolantTempSlider,
                    engineSpeedSlider,
                    ambientAirTempSlider,
                    intakeManifoldTempSlider,
                    coolantTempCheckBox,
                    engineSpeedCheckBox,
                    ambientAirTempCheckBox,
                    intakeManifoldTempCheckBox);
        });


        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> canMessages.stopSending());
//        stopButton.setOnAction(
//                coolantTempCheckBox.setSelected(false);
//                engineSpeedCheckBox.setSelected(false);
//                ambientAirTempCheckBox.setSelected(false);
//                intakeManifoldTempCheckBox.setSelected(false);
//                );



        // Обновление CAN-параметров при изменении значения ползунков или состояния флажков
        coolantTempSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                canMessages.updateCoolantTemperature(newValue.intValue()));
        coolantTempCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                canMessages.setCoolantTempActive(newValue));

        engineSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                canMessages.updateEngineSpeed(newValue.intValue()));
        engineSpeedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                canMessages.setEngineSpeedActive(newValue));

        ambientAirTempSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                canMessages.updateAmbientAirTemperature(newValue.intValue()));
        ambientAirTempCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                canMessages.setAmbientAirTempActive(newValue));

        intakeManifoldTempSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                canMessages.updateIntakeManifoldTemperature(newValue.intValue()));
        intakeManifoldTempCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                canMessages.setIntakeManifoldTempActive(newValue));

        // Сетка расположения элементов интерфейса
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Coolant Temp"), 0, 0);
        grid.add(coolantTempSlider, 1, 0);
        grid.add(coolantTempCheckBox, 2, 0);

//        grid.add(new Font(14));
        grid.add(new Label("Engine Speed"), 0, 1);
        grid.add(engineSpeedSlider, 1, 1);
        grid.add(engineSpeedCheckBox, 2, 1);

        grid.add(new Label("Ambient Air Temp"), 0, 2);
        grid.add(ambientAirTempSlider, 1, 2);
        grid.add(ambientAirTempCheckBox, 2, 2);

        grid.add(new Label("Intake Manifold Temp"), 0, 3);
        grid.add(intakeManifoldTempSlider, 1, 3);
        grid.add(intakeManifoldTempCheckBox, 2, 3);

        grid.add(startButton, 0, 4);
        grid.add(stopButton, 1, 4);

        Scene scene = new Scene(grid, 1200, 300);
        primaryStage.setScene(scene);
        primaryStage.show();


        coolantTempSlider.setPrefWidth(850);
        engineSpeedSlider.setPrefWidth(850);
        ambientAirTempSlider.setPrefWidth(850);
        intakeManifoldTempSlider.setPrefWidth(850);

    }

    private void updateCANParameters(Slider coolantTempSlider, Slider engineSpeedSlider,
                                     Slider ambientAirTempSlider, Slider intakeManifoldTempSlider,
                                     CheckBox coolantTempCheckBox, CheckBox engineSpeedCheckBox,
                                     CheckBox ambientAirTempCheckBox, CheckBox intakeManifoldTempCheckBox) {

        canMessages.updateCoolantTemperature((int) coolantTempSlider.getValue());
        canMessages.setCoolantTempActive(coolantTempCheckBox.isSelected());

        canMessages.updateEngineSpeed((int) engineSpeedSlider.getValue());
        canMessages.setEngineSpeedActive(engineSpeedCheckBox.isSelected());

        canMessages.updateAmbientAirTemperature((int) ambientAirTempSlider.getValue());
        canMessages.setAmbientAirTempActive(ambientAirTempCheckBox.isSelected());

        canMessages.updateIntakeManifoldTemperature((int) intakeManifoldTempSlider.getValue());
        canMessages.setIntakeManifoldTempActive(intakeManifoldTempCheckBox.isSelected());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
