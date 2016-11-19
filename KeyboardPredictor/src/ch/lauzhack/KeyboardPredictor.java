package ch.lauzhack;

import com.logitech.gaming.LogiLED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class KeyboardPredictor {
	public static void main(String[] args) {
		KeyboardMessageDisplay keyboard = new KeyboardMessageDisplay();
		Predictor predictor = new Predictor(10);

		char last = ' ';
		char read = ' ';
		String text = "";

		LogiLED.LogiLedInit();
		LogiLED.LogiLedSetLighting(0, 0, 0);

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

		while (read != '\r' && read != '\n') {
            try {
                read = (char) input.read();
            } catch (IOException e) {
                break;
            }

            text += read;

            List<CharProbPair> letters = predictor.getNextChar(text);
            char key = letters.get(0).getChar();

            keyboard.ShowLetter(key, 100, 0, 0);
            keyboard.ShowLetter(last, 100, 0, 0);

            last = key;

            for (CharProbPair pair : letters) {
                System.out.print(String.format("%s (%.2f)", pair.getChar(), pair.getProbability()));
            }
            System.out.println();
        }
	}
}
