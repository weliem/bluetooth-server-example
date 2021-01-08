package com.welie.btserver;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.welie.btserver.PeripheralManager.CCC_DESCRIPTOR_UUID;
import static com.welie.btserver.PeripheralManager.CUD_DESCRIPTOR_UUID;

class BaseService implements Service {

    @NotNull
    protected final PeripheralManager peripheralManager;

    BaseService(@NotNull PeripheralManager peripheralManager) {
        this.peripheralManager = Objects.requireNonNull(peripheralManager);
    }

    BluetoothGattDescriptor getCccDescriptor() {
        BluetoothGattDescriptor cccDescriptor = new BluetoothGattDescriptor(CCC_DESCRIPTOR_UUID, BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        cccDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        return cccDescriptor;
    }

    BluetoothGattDescriptor getCudDescriptor(@NotNull String defaultValue) {
        Objects.requireNonNull(defaultValue, "CUD value is null");
        BluetoothGattDescriptor cudDescriptor = new BluetoothGattDescriptor(CUD_DESCRIPTOR_UUID, BluetoothGattDescriptor.PERMISSION_READ);
        cudDescriptor.setValue(defaultValue.getBytes(StandardCharsets.UTF_8));
        return cudDescriptor;
    }

    protected void notifyCharacteristicChanged(@NotNull final BluetoothGattCharacteristic characteristic) {
        peripheralManager.notifyCharacteristicChanged(characteristic);
    }

    boolean noCentralsConnected() {
        return peripheralManager.getConnectedCentrals().size() == 0;
    }

    @Override
    public BluetoothGattService getService() {
        return null;
    }

    @Override
    public void onCharacteristicRead(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public GattStatus onCharacteristicWrite(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic, @NotNull byte[] value) {
        return GattStatus.SUCCESS;
    }

    @Override
    public void onDescriptorRead(@NotNull Central central, @NotNull BluetoothGattDescriptor descriptor) {

    }

    @Override
    public GattStatus onDescriptorWrite(@NotNull Central central, @NotNull BluetoothGattDescriptor descriptor, @NotNull byte[] value) {
        return GattStatus.SUCCESS;
    }

    @Override
    public void onNotifyingEnabled(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onNotifyingDisabled(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onCentralConnected(@NotNull Central central) {

    }

    @Override
    public void onCentralDisconnected(@NotNull Central central) {

    }
}
