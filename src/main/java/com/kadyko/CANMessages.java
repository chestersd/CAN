package com.kadyko;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class CANMessages {

    // Интерфейс для работы с PCANBasic.dll через JNA
    public interface PCANBasic extends com.sun.jna.Library {
        PCANBasic INSTANCE = Native.load("PCANBasic", PCANBasic.class);

        int CAN_Initialize(int channel, int baudrate, int hwType, int ioPort, int interrupt);
        int CAN_Write(int channel, TPCANMsg msg);
        int CAN_Uninitialize(int channel);
    }

    // Структура для CAN-сообщения
    public static class TPCANMsg extends Structure {
        public int id;        // 29-битный идентификатор сообщения
        public byte msgType;  // Тип сообщения (стандартный или расширенный)
        public byte length;   // Длина сообщения в байтах
        public byte[] data = new byte[8];  // Данные сообщения (максимум 8 байт)

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("id", "msgType", "length", "data");
        }
    }

    // Идентификаторы сообщений
    private static final int COOLANT_TEMP_ID = 0x18FEEE00;
    private static final int ENGINE_SPEED_ID = 0x0CF00400;
    private static final int AMBIENT_TEMP_ID = 0x18FEF559;
    private static final int INTAKE_TEMP_ID = 0x18FEF600;

    // Параметры CAN-шины
    private static final int PCAN_USBBUS1 = 0x51;
    private static final int PCAN_BAUD_250K = 0x011C;
    private static final byte PCAN_MESSAGE_EXTENDED = 0x02;

    // Значения параметров
    private int coolantTemperature = 20;
    private int engineSpeed = 300;
    private int ambientAirTemperature = 20;
    private int intakeManifoldTemperature = 0;
    private boolean isSending = false;
    private Thread sendThread;

    // Активные состояния параметров
    private boolean isCoolantTempActive = true;
    private boolean isEngineSpeedActive = true;
    private boolean isAmbientAirTempActive = true;
    private boolean isIntakeManifoldTempActive = true;

    // Инициализация адаптера
    public CANMessages() {
        int result = PCANBasic.INSTANCE.CAN_Initialize(PCAN_USBBUS1, PCAN_BAUD_250K, 0, 0, 0);
        if (result != 0) {
            System.out.println("Ошибка инициализации CAN-адаптера. Код ошибки: " + result);
        }
    }

    public void startSending() {
        isSending = true;
        sendThread = new Thread(this::sendData);
        sendThread.start();
    }

    public void stopSending() {
        isSending = false;
        if (sendThread != null) {
            try {
                sendThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Методы для обновления значений и состояния параметров
    public void updateCoolantTemperature(int temperature) {
        coolantTemperature = temperature;
    }

    public void updateEngineSpeed(int speed) {
        engineSpeed = speed * 8;
    }

    public void updateAmbientAirTemperature(int temperature) {
        ambientAirTemperature = (temperature + 273) * 32;
    }

    public void updateIntakeManifoldTemperature(int temperature) {
        intakeManifoldTemperature = temperature;
    }

    public void setCoolantTempActive(boolean active) {
        isCoolantTempActive = active;
    }

    public void setEngineSpeedActive(boolean active) {
        isEngineSpeedActive = active;
    }

    public void setAmbientAirTempActive(boolean active) {
        isAmbientAirTempActive = active;
    }

    public void setIntakeManifoldTempActive(boolean active) {
        isIntakeManifoldTempActive = active;
    }

    // Метод для отправки данных в CAN-шину
    private void sendData() {
        while (isSending) {
            if (isCoolantTempActive) {
                sendCANMessage(COOLANT_TEMP_ID, isCoolantTempActive ? new byte[]{(byte) (coolantTemperature + 40), 0, 0, 0, 0, 25, 0, 0} : new byte[8]);
            }
            if (isEngineSpeedActive) {
                sendCANMessage(ENGINE_SPEED_ID, isEngineSpeedActive ? new byte[]{0, 0, 0, (byte) (engineSpeed & 0xFF), (byte) (engineSpeed >> 8), 0,  0, 0} : new byte[8]);
            }
            if (isAmbientAirTempActive) {
                sendCANMessage(AMBIENT_TEMP_ID, isAmbientAirTempActive ? new byte[]{0, 0, 0, (byte) ((ambientAirTemperature) & 0xFF), (byte) ((ambientAirTemperature) >> 8), 0, 0, 0} : new byte[8]);
            }
            if (isIntakeManifoldTempActive) {
                sendCANMessage(INTAKE_TEMP_ID, isIntakeManifoldTempActive ? new byte[]{(byte) 0x80, (byte) 0x80, (byte) (intakeManifoldTemperature + 40), (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80} : new byte[8]);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        PCANBasic.INSTANCE.CAN_Uninitialize(PCAN_USBBUS1);
        System.out.println("CAN-адаптер был деинициализирован.");
    }

    // Отправка сообщения в CAN-шину
    private void sendCANMessage(int id, byte[] data) {

        TPCANMsg msg = new TPCANMsg();
        msg.id = id;
        msg.msgType = PCAN_MESSAGE_EXTENDED;
        msg.length = 8;
        msg.data = data;

        int result = PCANBasic.INSTANCE.CAN_Write(PCAN_USBBUS1, msg);
        if (result != 0) {
            System.out.println("Ошибка при отправке сообщения ID = 0x" + Integer.toHexString(id).toUpperCase());
        }
    }
}
