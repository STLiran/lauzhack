package ch.lauzhack;

import com.logitech.gaming.LogiLED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KeyboardPredictor {
	public static void main(String[] args) {

		LogiLED.LogiLedInit();
		LogiLED.LogiLedSetLighting(0, 0, 0);
		KeyboardMessageDisplay keyboard = new KeyboardMessageDisplay();
		Predictor predictor = new Predictor(10);

		char read = ' ';
		String text = "";
        List<CharProbPair> active = new ArrayList<>();

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        //BTDetector bt = new BTDetector();

		while (read != '0') {
            try {
                read = (char)input.read();
                if (read == '\r' || read == '\n')
                    continue;
            } catch (IOException e) {
                break;
            }

            text += read;

            List<CharProbPair> letters = predictor.getNextChar(text);

            // Turn off old keys
            for (CharProbPair old : active) {
                keyboard.ShowLetter(old.getChar(), 0, 0, 0);
            }

            active = letters.subList(0, 3);

            // Turn on new keys
            int intensity = 100;
            for (CharProbPair key : active) {
                keyboard.ShowLetter(key.getChar(), intensity, 0, 0);
                intensity -= 33;
            }

            for (CharProbPair pair : letters) {
                System.out.print(String.format("%s (%.2f)", pair.getChar(), pair.getProbability()));
            }
        }
	}
}
