package ch.lauzhack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loic on 20.11.2016.
 */
public class HistoryTree {
    private List<String> history;

    public HistoryTree() {
        history = new ArrayList<>();
        add("");
    }

    public void clear() {
        history.clear();
        add("");
    }

    public void add(String data) {
        history.add(data);
    }

    public void addChar(char c) {
        history.add(getString() + c);
    }

    public void erase() {
        if (history.size() > 1)
            history.remove(history.size() - 1);
    }

    public String getString() {
        return history.get(history.size() - 1);
    }
}
