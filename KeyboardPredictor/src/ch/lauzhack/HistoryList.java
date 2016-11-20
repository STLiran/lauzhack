package ch.lauzhack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loic on 20.11.2016.
 */
public class HistoryList {
    private String raw;
    private List<String> history;
    private String alphabet;

    public HistoryList(String alphabet) {
        this.history = new ArrayList<>();
        this.alphabet = alphabet;
        add("");
    }

    public void clear() {
        history.clear();
        raw = "";
        add("");
    }

    public void add(String data) {
        history.add(data);
        raw = data;
    }

    public void addChar(char c) {
        if (isInAlphabet(c))
            history.add(getString() + c);
        raw += c;
    }

    public void erase() {
        if (raw.length() > 0) {
            char c = raw.charAt(raw.length() - 1);
            raw = raw.substring(0, raw.length() - 1);

            if (isInAlphabet(c)) {
                if (history.size() > 1)
                    history.remove(history.size() - 1);
            }
        }
    }

    public boolean isInAlphabet(char c) {
        return alphabet.contains("" + c);
    }

    public String getString() {
        return history.get(history.size() - 1);
    }
}
