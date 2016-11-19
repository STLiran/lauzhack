package ch.lauzhack;

import de.ksquared.system.keyboard.GlobalKeyListener;
import de.ksquared.system.keyboard.KeyEvent;
import de.ksquared.system.keyboard.KeyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loic on 19.11.2016.
 */
public class BackgroundListener implements KeyListener {
    private GlobalKeyListener listener;
    private String text;
    private List<CharProbPair> active;
    private Predictor predictor;
    private KeyboardMessageDisplay keyboard;

    public BackgroundListener() {
        text = "";
        predictor = new Predictor(10);
        keyboard = new KeyboardMessageDisplay();
        listener = new GlobalKeyListener();
        active = new ArrayList<>();
        listener.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        char read = Character.toLowerCase((char)keyEvent.getVirtualKeyCode());
        if (!predictor.isValidChar(read) && !keyEvent.isAltPressed() && !keyEvent.isCtrlPressed()) {
            return;
        }

        text += Character.toLowerCase(read);

        System.out.println(text);

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
        System.out.println();
    }
}
