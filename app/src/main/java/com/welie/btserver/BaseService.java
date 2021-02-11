package com.welie.btserver;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothPeripheralManager;
import com.welie.blessed.GattStatus;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_READ;
import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_WRITE;

class BaseService implements Service {

    public static final UUID CUD_DESCRIPTOR_UUID = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    public static final UUID CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @NotNull
    protected final BluetoothPeripheralManager peripheralManager;

    BaseService(@NotNull BluetoothPeripheralManager peripheralManager) {
        this.peripheralManager = Objects.requireNonNull(peripheralManager);
    }

    BluetoothGattDescriptor getCccDescriptor() {
        BluetoothGattDescriptor cccDescriptor = new BluetoothGattDescriptor(CCC_DESCRIPTOR_UUID, PERMISSION_READ | PERMISSION_WRITE);
        cccDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        return cccDescriptor;
    }

    BluetoothGattDescriptor getCudDescriptor(@NotNull String defaultValue) {
        Objects.requireNonNull(defaultValue, "CUD value is null");
        BluetoothGattDescriptor cudDescriptor = new BluetoothGattDescriptor(CUD_DESCRIPTOR_UUID, PERMISSION_READ | PERMISSION_WRITE);
        cudDescriptor.setValue(defaultValue.getBytes(StandardCharsets.UTF_8));
        return cudDescriptor;
    }

    protected void notifyCharacteristicChanged(@NotNull final byte[] value, @NotNull final BluetoothGattCharacteristic characteristic) {
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
    public void onCharacteristicRead(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public GattStatus onCharacteristicWrite(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic, @NotNull byte[] value) {
        return GattStatus.SUCCESS;
    }

    @Override
    public void onDescriptorRead(@NotNull BluetoothCentral central, @NotNull BluetoothGattDescriptor descriptor) {

    }

    @Override
    public GattStatus onDescriptorWrite(@NotNull BluetoothCentral central, @NotNull BluetoothGattDescriptor descriptor, @NotNull byte[] value) {
        return GattStatus.SUCCESS;
    }

    @Override
    public void onNotifyingEnabled(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onNotifyingDisabled(@NotNull BluetoothCentral central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onNotificationSent(@NotNull BluetoothCentral central, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
    }

    @Override
    public void onCentralConnected(@NotNull BluetoothCentral central) {

    }

    @Override
    public void onCentralDisconnected(@NotNull BluetoothCentral central) {

    }
}
