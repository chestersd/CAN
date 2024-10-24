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
        public short[] data = new short[8];  // Данные сообщения

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("id", "msgType", "length", "data");
        }
    }

    // Идентификаторы сообщений
    private static final int[] messageIDs = {
            0x18FEEE00,  // Сообщение 1
            0x0CF00400,  // Сообщение 2
            0x18FEF559,  // Сообщение 3
            0x18FEF600   // Сообщение 4
    };

    // Параметры CAN-шины
    private static final int PCAN_USBBUS1 = 0x51;
    private static final int PCAN_BAUD_250K = 0x011C;
    private static final byte PCAN_MESSAGE_EXTENDED = 0x02;

    // Переменные для передачи данных
    private static int coolantTemperature = 15;
    private static int engineSpeed = 0;
    private static int ambientAirTemperature = -40;
    private static int intakeManifoldTemperature = -40;
    private static boolean isSending = false;
    private Thread sendThread;

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
                sendThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Обновление значений
    public void updateCoolantTemperature(int temperature) {
        coolantTemperature = temperature;
    }

    public void updateEngineSpeed(int speed) {
        engineSpeed = speed;
    }

    public void updateAmbientAirTemperature(int temperature) {
        ambientAirTemperature = temperature;
    }

    public void updateIntakeManifoldTemperature(int temperature) {
        intakeManifoldTemperature = temperature;
    }

    // Метод для отправки данных в CAN-шину
    private void sendData() {
        while (isSending) {
            for (int id : messageIDs) {
                TPCANMsg msg = new TPCANMsg();
                msg.id = id;
                msg.msgType = PCAN_MESSAGE_EXTENDED;
                msg.length = 8;

                if (msg.id == 0x18FEEE00) {
                    msg.data[0] = (byte) (coolantTemperature + 40);
                    msg.data[1] = 0;
                    msg.data[2] = 0;
                    msg.data[3] = 0;
                    msg.data[4] = 0;
                    msg.data[5] = 25;
                    msg.data[6] = 0;
                    msg.data[7] = 0;
                } else if (msg.id == 0x0CF00400) {
                    msg.data[0] = 0;
                    msg.data[1] = 0;
                    msg.data[2] = 0;
                    msg.data[3] = (byte) (engineSpeed >> 8);
                    msg.data[4] = (byte) (engineSpeed & 0xFF);
                    msg.data[5] = 0;
                    msg.data[6] = 0;
                    msg.data[7] = 0;
                } else if (msg.id == 0x18FEF559) {
                    msg.data[0] = 0;
                    msg.data[1] = 0;
                    msg.data[2] = 0;
                    msg.data[3] = (byte) (engineSpeed >> 8); //???
                    msg.data[4] = (byte) (engineSpeed & 0xFF); //???
                    msg.data[5] = 0;
                    msg.data[6] = 0;
                    msg.data[7] = 0;
                } else if (msg.id == 0x18FEF600) {
                    msg.data[0] = 128;
                    msg.data[1] = 128;
                    msg.data[2] = (byte) (intakeManifoldTemperature + 40);
                    msg.data[3] = 128;
                    msg.data[4] = 128;
                    msg.data[5] = 128;
                    msg.data[6] = 128;
                    msg.data[7] = 128;
                }
//                msg.data[0] = (byte) (coolantTemperature + 40);
//                msg.data[1] = (byte) (engineSpeed >> 8);
//                msg.data[2] = (byte) (engineSpeed & 0xFF);
//                msg.data[3] = (byte) (ambientAirTemperature + 40);
//                msg.data[4] = (byte) (intakeManifoldTemperature + 40);
//                msg.data[5] = 19;
//                msg.data[6] = 0;
//                msg.data[7] = 0;

                int result = PCANBasic.INSTANCE.CAN_Write(PCAN_USBBUS1, msg);
                if (result != 0) {
                    System.out.println("Ошибка при отправке сообщения ID = 0x" + Integer.toHexString(id).toUpperCase());
                }
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
}
