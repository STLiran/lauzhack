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
    private List<CharProbPair> active;
    private HistoryTree history;
    private Predictor predictor;
    private KeyboardMessageDisplay keyboard;

    public BackgroundListener() {
        predictor = new Predictor(-1);
        keyboard = new KeyboardMessageDisplay();
        listener = new GlobalKeyListener();
        active = new ArrayList<>();
        history = new HistoryTree();
        listener.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int intRead = keyEvent.getVirtualKeyCode();
        char read = Character.toLowerCase((char)intRead);

        if (read == '\r' || read == '\n') {
            history.clear();
        }

        if (read != '\b' && (!predictor.isValidChar(read) || keyEvent.isAltPressed() || keyEvent.isCtrlPressed())) {
            return;
        }

        if (read == ' ') {
            history.clear();
        } else if (read == '\b') {      // Backspace detection
            history.erase();
        } else {
            history.addChar(read);
        }

        List<CharProbPair> letters = predictor.getNextChar(history.getString());

        // No char predict ? Reset prediction
        CharProbPair first = letters.get(0);
        if (first.getProbability() == 0) {
            history.erase();
            history.addChar(read);
            letters = predictor.getNextChar(history.getString());
        }

        System.out.println("Input: " + history.getString());

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
        //if (first.getChar() == ' ' && first.getProbability() == 1) {
        //    history.clear();
        //}

        for (CharProbPair pair : letters) {
            System.out.print(String.format("%s (%.2f)", pair.getChar(), pair.getProbability()));
        }
        System.out.println();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }
}
