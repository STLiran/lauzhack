package proxi;

import java.io.IOException;
import javax.bluetooth.*;
import java.util.ArrayList;

/**
 * Minimal Device Discovery example.
 */
public class BeaconDiscovery {

    public static final ArrayList<RemoteDevice> devicesDiscovered = new ArrayList<RemoteDevice>();

    public static void main(String[] args) throws IOException, InterruptedException {

        final Object inquiryCompletedEvent = new Object();

        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            	if (!devicesDiscovered.contains(btDevice)) {
	                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
	                devicesDiscovered.add(btDevice);
	                try {
	                    System.out.println("     name " + btDevice.getFriendlyName(false));
	                } catch (IOException cantGetDeviceName) {
	                }
            	}
            }

            public void inquiryCompleted(int discType) {
                System.out.println("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        };

        while (true) {
	        synchronized(inquiryCompletedEvent) {
	            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
	            if (started) {
	                System.out.println("wait for device inquiry to complete...");
	                inquiryCompletedEvent.wait();
	                System.out.println(devicesDiscovered.size() +  " device(s) found");
	            }
	        }
        }
    }
}