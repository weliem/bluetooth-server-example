package com.welie.btserver;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.AdvertiseSettings;

import org.jetbrains.annotations.NotNull;

/**
 * Callbacks for the BluetoothPeripheralManager class
 */
public abstract class BluetoothPeripheralManagerCallback {

    /**
     * Indicates whether a local service has been added successfully.
     *
     * @param status  Returns {@link BluetoothGatt#GATT_SUCCESS} if the service was added
     *                successfully.
     * @param service The service that has been added
     */
    void onServiceAdded(int status, @NotNull BluetoothGattService service) {
    }

    /**
     * A remote central has requested to read a local characteristic.
     *
     * <p>This callback is called before the current value of the characteristic is returned to the central.
     * Therefore, any modifications to the characteristic value can still be made.
     * If the characteristic's value is longer than the MTU - 1 bytes, a long read will be executed automatically</p>
     *
     * @param central the central that is doing the request
     * @param characteristic the characteristic to be read
     */
    void onCharacteristicRead(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {
    }

    /**
     * A remote central has requested to write a local characteristic.
     *
     * <p>This callback is called before the current value of the characteristic is set to {@code value}.
     * The value should be checked and a GattStatus should be returned. If anything else than GattStatus.SUCCESS is returned,
     * the characteristic's value will not be updated.</p>
     *
     * <p>The value may be up to 512 bytes (in case of a long write)</p>
     *
     * @param central the central that is doing the request
     * @param characteristic the characteristic to be written
     * @param value the value the central wants to write
     * @return GattStatus.SUCCESS if the value is acceptable, otherwise an appropriate status
     */
    GattStatus onCharacteristicWrite(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic, @NotNull byte[] value) {
        return GattStatus.SUCCESS;
    }

    /**
     * A remote central has requested to read a local descriptor.
     *
     * <p>This callback is called before the current value of the descriptor is returned to the central.
     * Therefore, any modifications to the characteristic value can still be made.
     * If the descriptor's value is longer than the MTU - 1 bytes, a long read will be executed automatically</p>
     *
     * @param central the central that is doing the request
     * @param descriptor the descriptor to be read
     */
    void onDescriptorRead(@NotNull Central central, @NotNull BluetoothGattDescriptor descriptor) {
    }

    /**
     * A remote central has requested to write a local descriptor.
     *
     * <p>This callback is called before the current value of the descriptor is set to {@code value}.
     * The value should be checked and a GattStatus should be returned. If anything else than GattStatus.SUCCESS is returned,
     * the descriptor's value will not be updated.</p>
     *
     * <p>The value may be up to 512 bytes (in case of a long write)</p>
     *
     * @param central the central that is doing the request
     * @param descriptor the descriptor to be written
     * @param value the value the central wants to write
     * @return GattStatus.SUCCESS if the value is acceptable, otherwise an appropriate status
     */
    GattStatus onDescriptorWrite(@NotNull Central central, @NotNull BluetoothGattDescriptor descriptor, @NotNull byte[] value) {
        return GattStatus.SUCCESS;
    }

    /**
     * A remote central has enabled notifications or indications for a characteristic
     *
     * @param central the central
     * @param characteristic the characteristic
     */
    void onNotifyingEnabled(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {
    }

    /**
     * A remote central has disabled notifications or indications for a characteristic
     *
     * @param central the central
     * @param characteristic the characteristic
     */
    void onNotifyingDisabled(@NotNull Central central, @NotNull BluetoothGattCharacteristic characteristic) {
    }

    /**
     * A notification has been sent to a central
     *
     * @param central the central
     * @param characteristic the characteristic for which the notifcation was sent
     */
    void onNotificationSent(@NotNull Central central, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
    }

    /**
     * A remote central has connected
     *
     * @param central the central
     */
    void onCentralConnected(@NotNull Central central) {
    }

    /**
     * A remote central has disconnected
     *
     * @param central the central
     */
    void onCentralDisconnected(@NotNull Central central) {
    }

    /**
     * Advertising has successfully start
     *
     * @param settingsInEffect the AdvertiseSettings that are currently active
     */
    void onAdvertisingStarted(@NotNull AdvertiseSettings settingsInEffect) {
    }

    /**
     * Advertising has failed
     *
     * @param advertiseError the error explaining why the advertising failed
     */
    void onAdvertiseFailure(@NotNull AdvertiseError advertiseError) {
    }

    /**
     * Advertising has stopped
     *
     */
    void onAdvertisingStopped() {
    }
}

