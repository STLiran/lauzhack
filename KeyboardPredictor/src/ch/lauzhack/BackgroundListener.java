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
        predictor = new Predictor(-1);
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
        
        if (read == '\r' || read == '\n') {
            text = "";
        }
        
        if (!predictor.isValidChar(read) || keyEvent.isAltPressed() || keyEvent.isCtrlPressed()) {
            return;
        }

        if (read == ' ') {
            text = "";
        } else {
            text += Character.toLowerCase(read);
        }

        List<CharProbPair> letters = predictor.getNextChar(text);

        // No char predict ? Reset prediction
        CharProbPair first = letters.get(0);
        if (first.getProbability() == 0) {
            text = "" + read;
            letters = predictor.getNextChar(text);
        }

        System.out.println("Input: " + text);

        // Turn off old keys
        for (CharProbPair old : active) {
            keyboard.ShowLetter(old.getChar(), 0, 0, 0);
        }

        active.clear();

        for (CharProbPair key : letters) {
            if (key.getProbability() >= 0.01) {
                active.add(key);
            }
        }

        // Turn on new keys
        int intensity = 100;
        for (CharProbPair key : active) {
            keyboard.ShowLetter(key.getChar(), 0, intensity, 0);
        }

        // Reached end of word
        if (first.getChar() == ' ' && first.getProbability() == 1) {
            text = "";
        }

        for (CharProbPair pair : letters) {
            System.out.print(String.format("%s (%.2f)", pair.getChar(), pair.getProbability()));
        }
        System.out.println();
    }
}
