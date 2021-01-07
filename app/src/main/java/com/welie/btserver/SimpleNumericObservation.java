package com.welie.btserver;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.welie.btserver.BluetoothBytesParser.FORMAT_UINT16;
import static com.welie.btserver.BluetoothBytesParser.FORMAT_UINT32;
import static com.welie.btserver.BluetoothBytesParser.mergeArrays;

public class SimpleNumericObservation {


    private final byte[] handle;
    private final byte[] type;
    private final byte[] value;
    private final byte[] unit;
    private final byte[] timestamp;

    SimpleNumericObservation(@NotNull byte[] handle, @NotNull byte[] type, @NotNull byte[] value, @NotNull byte[] unit, @NotNull byte[] timestamp) {
        this.handle = Objects.requireNonNull(handle);
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
        this.unit = Objects.requireNonNull(unit);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    public byte[] getBytes() {
        return mergeArrays(handle, type, value, unit, timestamp);
    }

    public static final class Builder {

        int handleCode = 0x00010921;
        int handleLength = 2;
        Short handleValue;

        int typeCode = 0x0001092F;
        int typeLength = 4;
        ObservationType type;

        int valueCode = 0x00010A56;
        int valueLength = 4;
        Integer intValue;
        Float floatValue;

        int unitCode = 0x00010996;
        int unitLength = 4;
        Unit unit;

        int timestampCode = 0x00010990;
        int timestampLength = 8;
        Date timestamp;

        public SimpleNumericObservation.Builder setObservationType(ObservationType type) {
            this.type = type;
            return this;
        }

        public SimpleNumericObservation.Builder setIntegerValue(Integer value) {
            this.intValue = value;
            return this;
        }

        public SimpleNumericObservation.Builder setFloatValue(Float value) {
            this.floatValue = value;
            return this;
        }

        public SimpleNumericObservation.Builder setUnit(Unit unit) {
            this.unit = unit;
            return this;
        }

        public SimpleNumericObservation.Builder setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SimpleNumericObservation.Builder setHandle(Short id) {
            this.handleValue = id;
            return this;
        }


        /**
         * Build the {@link SimpleNumericObservation} object.
         */
        public SimpleNumericObservation build() {
            BluetoothBytesParser handleParser = new BluetoothBytesParser(ByteOrder.BIG_ENDIAN);
            handleParser.setIntValue(handleCode, FORMAT_UINT32);
            handleParser.setIntValue(handleLength, FORMAT_UINT16);
            handleParser.setIntValue(handleValue, FORMAT_UINT16);

            BluetoothBytesParser typeParser = new BluetoothBytesParser(ByteOrder.BIG_ENDIAN);
            typeParser.setIntValue(typeCode, FORMAT_UINT32);
            typeParser.setIntValue(typeLength, FORMAT_UINT16);
            typeParser.setIntValue(type.getValue(), FORMAT_UINT32);

            BluetoothBytesParser valueParser = new BluetoothBytesParser(ByteOrder.BIG_ENDIAN);
            valueParser.setIntValue(valueCode, FORMAT_UINT32);
            valueParser.setIntValue(valueLength, FORMAT_UINT16);
            if (intValue != null) {
                valueParser.setIntValue(intValue, FORMAT_UINT32);
            } else if (floatValue != null) {
                valueParser.setFloatValue(floatValue, 1);
            }

            BluetoothBytesParser unitParser = new BluetoothBytesParser(ByteOrder.BIG_ENDIAN);
            unitParser.setIntValue(unitCode, FORMAT_UINT32);
            unitParser.setIntValue(unitLength,FORMAT_UINT16 );
            unitParser.setIntValue(unit.getValue(), FORMAT_UINT32);

            BluetoothBytesParser timestampParser = new BluetoothBytesParser(ByteOrder.BIG_ENDIAN);
            timestampParser.setIntValue(timestampCode, FORMAT_UINT32);
            timestampParser.setIntValue(timestampLength, FORMAT_UINT16);
            timestampParser.setLong(Calendar.getInstance().getTimeInMillis());

            return new SimpleNumericObservation(
                    handleParser.getValue(),
                    typeParser.getValue(),
                    valueParser.getValue(),
                    unitParser.getValue(),
                    timestampParser.getValue());
        }
    }
}
