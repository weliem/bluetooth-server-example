package com.welie.btserver;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Looper;

import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothPeripheralManager;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.UUID;

import timber.log.Timber;

import static android.bluetooth.BluetoothGattCharacteristic.*;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;

class CurrentTimeService extends BaseService {

    private static final UUID CTS_SERVICE_UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    private static final UUID CURRENT_TIME_CHARACTERISTIC_UUID = UUID.fromString("00002A2B-0000-1000-8000-00805f9b34fb");

    private @NotNull final BluetoothGattService service = new BluetoothGattService(CTS_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
    private @NotNull final BluetoothGattCharacteristic currentTime = new BluetoothGattCharacteristic(CURRENT_TIME_CHARACTERISTIC_UUID, PROPERTY_READ | PROPERTY_INDICATE, PERMISSION_READ);
    private @NotNull final Handler handler = new Handler(Looper.getMainLooper());
    private @NotNull final Runnable notifyRunnable = this::notifyCurrentTime;

    public CurrentTimeService(@NotNull BluetoothPeripheralManager peripheralManager) {
        super(peripheralManager);
        service.addCharacteristic(currentTime);
        currentTime.addDescriptor(getCccDescriptor());
        currentTime.setValue(getCurrentTime());
    }

    @Override
    public void onCentralDisconnected(@NotNull BluetoothCentral central) {
        if (noCentralsConnected()) {
            stopNotifying();
        }
    }

    @Override
    public void onCharacteristicRead(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {
        currentTime.setValue(getCurrentTime());
    }

    @Override
    public void onNotifyingEnabled(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(CURRENT_TIME_CHARACTERISTIC_UUID)) {
            notifyCurrentTime();
        }
    }

    @Override
    public void onNotifyingDisabled(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(CURRENT_TIME_CHARACTERISTIC_UUID)) {
            stopNotifying();
        }
    }

    private void notifyCurrentTime() {
        notifyCharacteristicChanged(getCurrentTime() ,currentTime);
        handler.postDelayed(notifyRunnable, 1000);
    }

    private void stopNotifying() {
        handler.removeCallbacks(notifyRunnable);
    }

    @NotNull
    private byte[] getCurrentTime() {
        BluetoothBytesParser parser = new BluetoothBytesParser(ByteOrder.LITTLE_ENDIAN);
        parser.setCurrentTime(Calendar.getInstance());
        return parser.getValue();
    }

    @Override
    public @NotNull BluetoothGattService getService() {
        return service;
    }

    @Override
    public String getServiceName() {
        return "Current Time Service";
    }
}
