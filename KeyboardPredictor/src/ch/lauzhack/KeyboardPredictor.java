package ch.lauzhack;

import com.logitech.gaming.LogiLED;

public class KeyboardPredictor {
	public static void main(String[] args) {
		LogiLED.LogiLedInit();
		LogiLED.LogiLedSetLighting(0, 0, 0);

        BackgroundListener listener = new BackgroundListener();

        try {
            System.in.read();
        } catch (Exception e) {
        }
	}
}
