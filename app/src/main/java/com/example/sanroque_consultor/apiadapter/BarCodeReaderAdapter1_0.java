package com.example.sanroque_consultor.apiadapter;



import com.example.sanroque_consultor.register.barcodereader.BarcodeReader;
import com.elo.device.peripherals.BarCodeReader;

/**
 * Created by elo on 14/9/17.
 */

public class BarCodeReaderAdapter1_0 implements BarCodeReaderAdapter {

    private BarcodeReader barcodeReader;

    BarCodeReaderAdapter1_0() {
        this.barcodeReader = new BarcodeReader();
    }

    @Override
    public boolean isBarCodeReaderEnabled() {
        return false;
    }

    @Override
    public void setBarCodeReaderEnabled(boolean enabled) {
        barcodeReader.turnOnLaser();
    }

    @Override
    public boolean isBarCodeReaderKbdMode() {
        // Numa device is always in keyboard mode
        return true;
    }

    @Override
    public void setBarCodeReaderKbdMode() {
        // Numa device is always in keyboard mode
    }

    @Override
    public void setBarCodeReaderCallback(BarCodeReader.BarcodeReadCallback callback) {

    }

    @Override
    public byte[] sendCommand(byte[] cmd) {
        return new byte[0];
    }
}
