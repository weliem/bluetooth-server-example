package com.welie.btserver

import java.nio.ByteOrder

class ObservationSerializer {

    companion object {

        @JvmStatic
        fun serialize(simpleNumericObservation: SimpleNumericObservation) : ByteArray {
            val handleCode = 0x00010921
            val handleLength = 2

            val typeCode = 0x0001092F
            val typeLength = 4

            val valueCode = 0x00010A56
            val valueLength = 4

            val unitCode = 0x00010996
            val unitLength = 4

            val timestampCode = 0x00010990
            val timestampLength = 8

            val handleParser = BluetoothBytesParser(ByteOrder.BIG_ENDIAN)
            handleParser.setIntValue(handleCode, BluetoothBytesParser.FORMAT_UINT32)
            handleParser.setIntValue(handleLength, BluetoothBytesParser.FORMAT_UINT16)
            handleParser.setIntValue(simpleNumericObservation.id.toInt(), BluetoothBytesParser.FORMAT_UINT16)

            val typeParser = BluetoothBytesParser(ByteOrder.BIG_ENDIAN)
            typeParser.setIntValue(typeCode, BluetoothBytesParser.FORMAT_UINT32)
            typeParser.setIntValue(typeLength, BluetoothBytesParser.FORMAT_UINT16)
            typeParser.setIntValue(simpleNumericObservation.type.value, BluetoothBytesParser.FORMAT_UINT32)

            val valueParser = BluetoothBytesParser(ByteOrder.BIG_ENDIAN)
            valueParser.setIntValue(valueCode, BluetoothBytesParser.FORMAT_UINT32)
            valueParser.setIntValue(valueLength, BluetoothBytesParser.FORMAT_UINT16)
            valueParser.setFloatValue(simpleNumericObservation.value, 1)

            val unitParser = BluetoothBytesParser(ByteOrder.BIG_ENDIAN)
            unitParser.setIntValue(unitCode, BluetoothBytesParser.FORMAT_UINT32)
            unitParser.setIntValue(unitLength, BluetoothBytesParser.FORMAT_UINT16)
            unitParser.setIntValue(simpleNumericObservation.unit.value, BluetoothBytesParser.FORMAT_UINT32)

            val timestampParser = BluetoothBytesParser(ByteOrder.BIG_ENDIAN)
            timestampParser.setIntValue(timestampCode, BluetoothBytesParser.FORMAT_UINT32)
            timestampParser.setIntValue(timestampLength, BluetoothBytesParser.FORMAT_UINT16)
            timestampParser.setLong(simpleNumericObservation.timestamp.time)

            return BluetoothBytesParser.mergeArrays(
                    handleParser.value,
                    typeParser.value,
                    valueParser.value,
                    unitParser.value,
                    timestampParser.value
            )
        }
     }
}