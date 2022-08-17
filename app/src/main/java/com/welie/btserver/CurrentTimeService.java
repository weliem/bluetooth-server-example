package com.welie.btserver;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Looper;
import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothPeripheralManager;
import com.welie.blessed.GattStatus;
import com.welie.blessed.ReadResponse;

import org.jetbrains.annotations.NotNull;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.*;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;

import androidx.annotation.NonNull;

import timber.log.Timber;

class CurrentTimeService extends BaseService {

    private static final UUID CTS_SERVICE_UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    private static final UUID CURRENT_TIME_CHARACTERISTIC_UUID = UUID.fromString("00002A2B-0000-1000-8000-00805f9b34fb");

    private @NotNull final BluetoothGattService service = new BluetoothGattService(CTS_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
    private @NotNull final BluetoothGattCharacteristic currentTime = new BluetoothGattCharacteristic(CURRENT_TIME_CHARACTERISTIC_UUID, PROPERTY_READ | PROPERTY_NOTIFY | PROPERTY_WRITE, PERMISSION_READ | PERMISSION_WRITE);
    private @NotNull final Handler handler = new Handler(Looper.getMainLooper());
    private @NotNull final Runnable notifyRunnable = this::notifyCurrentTime;
    private long offset;

    public CurrentTimeService(@NotNull BluetoothPeripheralManager peripheralManager) {
        super(peripheralManager);
        service.addCharacteristic(currentTime);
        currentTime.addDescriptor(getCccDescriptor());
        currentTime.addDescriptor(getCudDescriptor());
    }

    @Override
    public void onCentralDisconnected(@NotNull BluetoothCentral central) {
        if (noCentralsConnected()) {
            stopNotifying();
        }
    }

    @Override
    public ReadResponse onCharacteristicRead(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {
        return new ReadResponse(GattStatus.SUCCESS, getCurrentTime());
    }

    @Override
    public GattStatus onCharacteristicWrite(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
        if (value.length != 10) return GattStatus.VALUE_NOT_ALLOWED;

        BluetoothBytesParser parser = new BluetoothBytesParser(value, ByteOrder.LITTLE_ENDIAN);
        Date date = parser.getDateTime();
        offset = Calendar.getInstance().getTime().getTime() - date.getTime();
        return super.onCharacteristicWrite(central, characteristic, value);
    }

    @Override
    public void onCharacteristicWriteCompleted(@NonNull BluetoothCentral central, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
        Timber.d("current time offset updated to %d", offset);
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

    private byte[] getCurrentTime() {
        BluetoothBytesParser parser = new BluetoothBytesParser(ByteOrder.LITTLE_ENDIAN);
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        date.setTime(date.getTime() - offset);
        cal.setTime(date);
        parser.setCurrentTime(cal);
        Timber.i("current time bytes %s", BluetoothBytesParser.bytes2String(parser.getValue()));
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
