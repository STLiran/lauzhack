package ch.lauzhack;

import java.io.IOException;
import javax.bluetooth.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal Device Discovery example.
 */
public class BeaconDiscovery {

	private static ArrayList<RemoteDevice> devicesDiscovered, devicesMemorized;
	private static DiscoveryListener listener;
	
	private String _stoppingCondition;
	private boolean stop = false;

    private final static Object inquiryCompletedEvent = new Object();
    
    private int counter = 0;
	
	public BeaconDiscovery(String stoppingCondition) {
		_stoppingCondition = new String(stoppingCondition);
		
		devicesDiscovered = new ArrayList<RemoteDevice>();
	    devicesDiscovered.clear();
	    devicesMemorized = new ArrayList<RemoteDevice>();
	    devicesMemorized.clear();
	
	    // Listener on bluetooth devices discovery events
	    listener = new DiscoveryListener() {
	        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                devicesDiscovered.add(btDevice);
	        	if (!devicesMemorized.contains(btDevice)) {
	                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
	                devicesMemorized.add(btDevice);
	                try {
	                	String name = btDevice.getFriendlyName(false);
	                    System.out.println("     name " + name);
	                    
	                    stop |= (name.equalsIgnoreCase(stoppingCondition));
	                } catch (IOException cantGetDeviceName) {
	                }
	        	}
	        }
	
	        public void inquiryCompleted(int discType) {
	            synchronized(inquiryCompletedEvent){
	            	for (RemoteDevice remoteDevice : devicesMemorized) {
						if (!devicesDiscovered.contains(remoteDevice)) {
							System.out.println("Lost device " + remoteDevice.getBluetoothAddress());
						}
					}

					devicesMemorized.clear();
					devicesMemorized.addAll(devicesDiscovered);
	            	
	                inquiryCompletedEvent.notifyAll();
	            }
	        }
	
	        public void serviceSearchCompleted(int transID, int respCode) {
	        }
	
	        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	        }
	    };
	}
	
	public void discover() throws InterruptedException, BluetoothStateException {
        synchronized(inquiryCompletedEvent) {
        	devicesDiscovered.clear();
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
            if (started) {
                inquiryCompletedEvent.wait();
                try {
                	if (!stop)
                		discover();
				} catch (BluetoothStateException | InterruptedException e) {
					e.printStackTrace();
				}
            }
        }
	}
}