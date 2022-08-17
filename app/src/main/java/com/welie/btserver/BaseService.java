package com.welie.btserver;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothPeripheralManager;
import com.welie.blessed.GattStatus;
import com.welie.blessed.ReadResponse;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_READ;
import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_WRITE;

import androidx.annotation.NonNull;

class BaseService implements Service {

    public static final UUID CUD_DESCRIPTOR_UUID = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    public static final UUID CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @NotNull
    protected final BluetoothPeripheralManager peripheralManager;

    BaseService(@NotNull BluetoothPeripheralManager peripheralManager) {
        this.peripheralManager = Objects.requireNonNull(peripheralManager);
    }

    BluetoothGattDescriptor getCccDescriptor() {
        return new BluetoothGattDescriptor(CCC_DESCRIPTOR_UUID, PERMISSION_READ | PERMISSION_WRITE);
    }

    BluetoothGattDescriptor getCudDescriptor() {
        return new BluetoothGattDescriptor(CUD_DESCRIPTOR_UUID, PERMISSION_READ | PERMISSION_WRITE);
    }

    protected void notifyCharacteristicChanged(final byte[] value, @NotNull final BluetoothGattCharacteristic characteristic) {
        peripheralManager.notifyCharacteristicChanged(value, characteristic);
    }

    boolean noCentralsConnected() {
        return peripheralManager.getConnectedCentrals().size() == 0;
    }

    @Override
    public BluetoothGattService getService() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public ReadResponse onCharacteristicRead(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {
        return new ReadResponse(GattStatus.REQUEST_NOT_SUPPORTED, null);
    }

    @Override
    public GattStatus onCharacteristicWrite(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic, byte[] value) {
        return GattStatus.SUCCESS;
    }

    @Override
    public void onCharacteristicWriteCompleted(@NonNull BluetoothCentral central, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {}

    @Override
    public ReadResponse onDescriptorRead(@NotNull BluetoothCentral central, @NotNull BluetoothGattDescriptor descriptor) {
        return new ReadResponse(GattStatus.REQUEST_NOT_SUPPORTED, null);
    }

    @Override
    public GattStatus onDescriptorWrite(@NotNull BluetoothCentral central, @NotNull BluetoothGattDescriptor descriptor, byte[] value) {
        return GattStatus.SUCCESS;
    }

    @Override
    public void onNotifyingEnabled(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onNotifyingDisabled(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onNotificationSent(@NotNull BluetoothCentral central, byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
    }

    @Override
    public void onCentralConnected(@NotNull BluetoothCentral central) {

    }

    @Override
    public void onCentralDisconnected(@NotNull BluetoothCentral central) {

    }
}
