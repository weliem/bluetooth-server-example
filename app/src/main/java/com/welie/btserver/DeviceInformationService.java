package com.welie.btserver;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import com.welie.blessed.BluetoothPeripheralManager;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;


class DeviceInformationService extends BaseService {

    private static final UUID DIS_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    private static final UUID MANUFACTURER_NAME_CHARACTERISTIC_UUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
    private static final UUID MODEL_NUMBER_CHARACTERISTIC_UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");

    private @NotNull final BluetoothGattService service = new BluetoothGattService(DIS_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

    public DeviceInformationService(@NotNull BluetoothPeripheralManager peripheralManager) {
        super(peripheralManager);

        final BluetoothGattCharacteristic manufacturer = new BluetoothGattCharacteristic(MANUFACTURER_NAME_CHARACTERISTIC_UUID, PROPERTY_READ, PERMISSION_READ);
        service.addCharacteristic(manufacturer);

        final BluetoothGattCharacteristic modelNumber = new BluetoothGattCharacteristic(MODEL_NUMBER_CHARACTERISTIC_UUID, PROPERTY_READ, PERMISSION_READ);
        service.addCharacteristic(modelNumber);

        manufacturer.setValue(Build.MANUFACTURER);
        modelNumber.setValue(Build.MODEL);
    }

    @Override
    public @NotNull BluetoothGattService getService() {
        return service;
    }

    @Override
    public String getServiceName() {
        return "Device Information Service";
    }
}
