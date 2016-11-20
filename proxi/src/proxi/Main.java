package proxi;

import javax.bluetooth.BluetoothStateException;

public class Main {

	public static void main(String[] args) {
		BeaconDiscovery bacon = new BeaconDiscovery();
		
		try {
			bacon.discover();
		} catch (BluetoothStateException | InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Goodbye");
	}

}
