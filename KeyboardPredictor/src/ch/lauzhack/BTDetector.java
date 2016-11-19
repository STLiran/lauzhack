package ch.lauzhack;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

/**
  * Created by Loic on 19.11.2016.
  */
class BTDetector {
    private LocalDevice device = null;

    public BTDetector() {
        try {
            device = LocalDevice.getLocalDevice();
        } catch (BluetoothStateException e) {
            System.out.println("Can't find bluetooth adapter");
        }
    }
}