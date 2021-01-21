package com.welie.btserver;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import timber.log.Timber;

class GenericHealthSensorService extends BaseService {

    private static final UUID GHS_SERVICE_UUID = UUID.fromString("0000183D-0000-1000-8000-00805f9b34fb");
    private static final UUID OBSERVATION_CHARACTERISTIC_UUID = UUID.fromString("00002AC4-0000-1000-8000-00805f9b34fb");
    private static final UUID CONTROL_POINT_CHARACTERISTIC_UUID = UUID.fromString("00002AC6-0000-1000-8000-00805f9b34fb");

    private static final String OBSERVATION_DESCRIPTION = "Characteristic for ACOM Observation segments.";
    private static final String CONTROL_POINT_DESCRIPTION = "Control point for generic health sensor.";

    @NotNull
    final BluetoothGattService service = new BluetoothGattService(GHS_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

    @NotNull
    final BluetoothGattCharacteristic observationCharacteristic = new BluetoothGattCharacteristic(OBSERVATION_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY, 0);

    @NotNull
    final BluetoothGattCharacteristic controlCharacteristic = new BluetoothGattCharacteristic(CONTROL_POINT_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_INDICATE, BluetoothGattCharacteristic.PERMISSION_WRITE);

    @NotNull
    private final Handler handler = new Handler(Looper.getMainLooper());

    @NotNull
    private final Runnable notifyRunnable = this::sendObservations;

    public GenericHealthSensorService(@NotNull BluetoothPeripheralManager peripheralManager) {
        super(peripheralManager);
        service.addCharacteristic(observationCharacteristic);
        service.addCharacteristic(controlCharacteristic);
        observationCharacteristic.addDescriptor(getCccDescriptor());
        observationCharacteristic.addDescriptor(getCudDescriptor(OBSERVATION_DESCRIPTION));
        observationCharacteristic.setValue(new byte[]{0x00});
        controlCharacteristic.addDescriptor(getCccDescriptor());
        controlCharacteristic.addDescriptor(getCudDescriptor(CONTROL_POINT_DESCRIPTION));
        controlCharacteristic.setValue(new byte[]{0x00});

        SimpleNumericObservation test2 = new SimpleNumericObservation((short) 1, ObservationType.ORAL_TEMPERATURE, 38.7f, Unit.CELSIUS, Calendar.getInstance().getTime());
        byte[] test2bytes = ObservationSerializer.serialize(test2);
        Timber.i("SimpleNumericObservation: <%s>", BluetoothBytesParser.bytes2String(test2bytes));

    }

    private byte[][] segments = {
            // Segment 1 - Type: Obs Type, Length: 4, value: Heart Rate
            {0x05, 0x00, 0x01, 0x09, 0x2F, 0x00, 0x04, 0x00, 0x02, 0x41, (byte) 0x82},
            // Segment 2 - Type: Handle, Length: 2, Value: 0x0001
            {0x08, 0x00, 0x01, 0x09, 0x21, 0x00, 0x02, 0x00, 0x01},
            // Segment 3 - Type: Simple Num, Length: 4, value: Current HR
            {0x0C, 0x00, 0x01, 0x0A, 0x56, 0x00, 0x04, 0x00, 0x00, 0x00, 0x40},
            // Segment 4 - Type: Unit Code, Length: 4, value: bpm 0x00040AA0
            {0x10, 0x00, 0x01, 0x09, (byte) 0x96, 0x00, 0x04, 0x00, 0x04, 0x0A, (byte) 0xA0},
            // Segment 5 - Abs Timestamp, Length: 8, value: Wed Dec 02 2020 15:42:54
            {0x16, 0x00, 0x01, 0x09, (byte) 0x90, 0x00, 0x08, 0x00, 0x00, 0x01, 0x76, 0x24, 0x1E, (byte) 0xE8, (byte) 0x82}
    };

    @Override
    public void onCentralDisconnected(@NotNull Central central) {
        if (noCentralsConnected()) {
            stopNotifying();
        }
    }

    @Override
    public void onNotifyingEnabled(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {
        sendObservations();
    }

    @Override
    public void onNotifyingDisabled(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {
        stopNotifying();
    }

    @Override
    public GattStatus onCharacteristicWrite(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic, @NotNull byte[] value) {
        // Check the value if it meets the requirements, if so return GattStatus.SUCCESS, otherwise an error status
        return super.onCharacteristicWrite(central, characteristic, value);
    }

    @Override
    public GattStatus onDescriptorWrite(@NotNull Central central, @NotNull BluetoothGattDescriptor descriptor, @NotNull byte[] value) {
        Timber.i("onDescriptorWrite %s", BluetoothBytesParser.bytes2String(value));
        return super.onDescriptorWrite(central, descriptor, value);
    }

    private void stopNotifying() {
        handler.removeCallbacks(notifyRunnable);
        observationCharacteristic.getDescriptor(CCC_DESCRIPTOR_UUID).setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    }

    private void sendObservations() {
        updateSegments();
        for(int i = 0; i < 5; i++){
            Timber.i("Sending <%s>", BluetoothBytesParser.bytes2String(segments[i]));

            observationCharacteristic.setValue(segments[i]);
            notifyCharacteristicChanged(observationCharacteristic);
        }

        handler.postDelayed(notifyRunnable, 5000);
    }


    private void updateSegments() {
        byte hr = 0x40;

        // Segment 3 - Type: Simple Num, Length: 4, value: Current HR
        segments[2] = new byte[]{0x0C, 0x00, 0x01, 0x0A, 0x56, 0x00, 0x04, 0x00, 0x00, 0x00, hr};

        // Segment 5 - Abs Timestamp, Length: 8, value: Current time in milliseconds since epoch
        long millis = new Date().getTime();
        byte[] milliBytes = ByteBuffer.allocate(8).putLong(millis).array();
        byte[] timeBytes = {0x16, 0x00, 0x01, 0x09, (byte) 0x90, 0x00, 0x08, milliBytes[0], milliBytes[1], milliBytes[2], milliBytes[3], milliBytes[4], milliBytes[5], milliBytes[6], milliBytes[7]};
        segments[4] = timeBytes;
    }

    @Override
    public @NotNull BluetoothGattService getService() {
        return service;
    }

    @Override
    public String getServiceName() {
        return "General Health Service";
    }
}
