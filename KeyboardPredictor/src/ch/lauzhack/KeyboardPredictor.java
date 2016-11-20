package ch.lauzhack;

import javax.bluetooth.BluetoothStateException;

import com.logitech.gaming.LogiLED;

public class KeyboardPredictor {
	public static void main(String[] args) {
		LogiLED.LogiLedInit();
		LogiLED.LogiLedSetLighting(100, 0, 0);

		BeaconDiscovery bacon = new BeaconDiscovery("VincePlus One");
		try {
			bacon.discover();
		} catch (BluetoothStateException | InterruptedException e1) {
			e1.printStackTrace();
		}

		LogiLED.LogiLedSetLighting(0, 0, 0);

        BackgroundListener listener = new BackgroundListener();

        try {
            System.in.read();
        } catch (Exception e) {
        }
	}
}
