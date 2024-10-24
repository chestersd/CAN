package com.kadyko;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GUI extends Application {

    private static int currentCoolantTemperature = 15;
    private static int currentEngineSpeed = 0;
    private static int currentAmbientAirTemperature = -40;
    private static int currentIntakeManifoldTemperature = -40;

    private CANMessages canMessages;

    @Override
    public void start(Stage primaryStage) {
        canMessages = new CANMessages();

        // Создание панели и элементов интерфейса
        GridPane grid = new GridPane();

        grid.setGridLinesVisible(true);
        
        Scene scene = new Scene(grid, 800, 700);
        primaryStage.setTitle("CAN Engine Data Sender");
        primaryStage.setScene(scene);

        // Ползунок для температуры охлаждающей жидкости
        Label coolantTempLabel = new Label("Температура охлаждающей жидкости (°C):");
        Slider coolantTempSlider = new Slider(0, 130, currentCoolantTemperature);
        coolantTempSlider.setShowTickLabels(true);
        coolantTempSlider.setShowTickMarks(true);
        coolantTempSlider.setMajorTickUnit(10);
        coolantTempSlider.setMinorTickCount(1);
        coolantTempSlider.setBlockIncrement(1);


        Label coolantTempValueLabel = new Label("Температура: " + currentCoolantTemperature + "°C");
        coolantTempSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentCoolantTemperature = newValue.intValue();
            coolantTempValueLabel.setText("Температура: " + currentCoolantTemperature + "°C");
            canMessages.updateCoolantTemperature(currentCoolantTemperature);
        });

        // Ползунок для оборотов двигателя
        Label engineSpeedLabel = new Label("Обороты двигателя (RPM):");
        Slider engineSpeedSlider = new Slider(0, 3000, currentEngineSpeed);
        engineSpeedSlider.setShowTickLabels(true);
        engineSpeedSlider.setShowTickMarks(true);
        engineSpeedSlider.setMajorTickUnit(500);
        engineSpeedSlider.setMinorTickCount(10);
        engineSpeedSlider.setBlockIncrement(50);

        Label engineSpeedValueLabel = new Label("Обороты: " + currentEngineSpeed + " RPM");
        engineSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentEngineSpeed = newValue.intValue();
            engineSpeedValueLabel.setText("Обороты: " + currentEngineSpeed + " RPM");
            canMessages.updateEngineSpeed(currentEngineSpeed);
        });

        // Ползунок для температуры окружающего воздуха
        Label ambientAirTempLabel = new Label("Температура окружающего воздуха (°C):");
        Slider ambientAirTempSlider = new Slider(-40, 50, currentAmbientAirTemperature);
        ambientAirTempSlider.setShowTickLabels(true);
        ambientAirTempSlider.setShowTickMarks(true);
        ambientAirTempSlider.setMajorTickUnit(10);
        ambientAirTempSlider.setMinorTickCount(1);
        ambientAirTempSlider.setBlockIncrement(5);

        Label ambientAirTempValueLabel = new Label("Температура: " + currentAmbientAirTemperature + "°C");
        ambientAirTempSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentAmbientAirTemperature = newValue.intValue();
            ambientAirTempValueLabel.setText("Температура: " + currentAmbientAirTemperature + "°C");
            canMessages.updateAmbientAirTemperature(currentAmbientAirTemperature);
        });

        // Ползунок для температуры впускного коллектора
        Label intakeManifoldTempLabel = new Label("Температура впускного коллектора (°C):");
        Slider intakeManifoldTempSlider = new Slider(-40, 200, currentIntakeManifoldTemperature);
        intakeManifoldTempSlider.setShowTickLabels(true);
        intakeManifoldTempSlider.setShowTickMarks(true);
        intakeManifoldTempSlider.setMajorTickUnit(20);
        intakeManifoldTempSlider.setMinorTickCount(1);
        intakeManifoldTempSlider.setBlockIncrement(10);

        Label intakeManifoldTempValueLabel = new Label("Температура: " + currentIntakeManifoldTemperature + "°C");
        intakeManifoldTempSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentIntakeManifoldTemperature = newValue.intValue();
            intakeManifoldTempValueLabel.setText("Температура: " + currentIntakeManifoldTemperature + "°C");
            canMessages.updateIntakeManifoldTemperature(currentIntakeManifoldTemperature);
        });

        // Кнопки запуска и остановки программы
        Button startButton = new Button("Запустить");
        startButton.setPrefSize(80, 30);
        startButton.setOnAction(e -> canMessages.startSending());

        Button stopButton = new Button("Остановить");
        stopButton.setPrefSize(80, 30);
        stopButton.setOnAction(e -> canMessages.stopSending());

        // Добавляем элементы на панель
        grid.add(coolantTempLabel, 0, 0);
        grid.add(coolantTempSlider, 0, 1);
        grid.add(coolantTempValueLabel, 0, 2);
        grid.add(engineSpeedLabel, 0, 3);
        grid.add(engineSpeedSlider, 0, 4);
        grid.add(engineSpeedValueLabel, 0, 5);
        grid.add(ambientAirTempLabel, 0, 6);
        grid.add(ambientAirTempSlider, 0, 7);
        grid.add(ambientAirTempValueLabel, 0, 8);
        grid.add(intakeManifoldTempLabel, 0, 9);
        grid.add(intakeManifoldTempSlider, 0, 10);
        grid.add(intakeManifoldTempValueLabel, 0, 11);
        grid.add(startButton, 0, 12);
        grid.add(stopButton, 0, 13);



        //My Code

        CheckBox checkBoxCoolantTemperature = new CheckBox("Вкл.");
        CheckBox checkBoxEngineSpeed = new CheckBox("Вкл.");
        CheckBox checkBoxAmbientAirTemperature = new CheckBox("Вкл.");
        CheckBox checkBoxIntakeManifoldTemperature = new CheckBox("Вкл.");

        checkBoxCoolantTemperature.setSelected(true);

        grid.add(checkBoxCoolantTemperature, 1, 1);
        grid.add(checkBoxEngineSpeed, 1, 4);
        grid.add(checkBoxAmbientAirTemperature, 1, 7);
        grid.add(checkBoxIntakeManifoldTemperature, 1, 10);

        // Отображение окна
        primaryStage.show();
    }
}
