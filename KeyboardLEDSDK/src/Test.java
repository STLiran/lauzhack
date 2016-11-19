import com.logitech.gaming.LogiLED;

public class Test {
	private final static int DURATION = 6000;
	public static void main(String[] args) {
		LogiLED.LogiLedInit();
		LogiLED.LogiLedSetLighting(100, 100, 100);
		LogiLED.LogiLedSaveCurrentLighting();
		KeyboardMessageDisplay messageDisplayer = new KeyboardMessageDisplay();
//		messageDisplayer.displayMessagePulse("hello Gallissard");
//		messageDisplayer.ShowLetter('a', 0, 100, 0);
		messageDisplayer.showHeart();
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
