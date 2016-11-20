package ch.lauzhack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.logitech.gaming.LogiLED;

/**
 * Created by Loic on 19.11.2016.
 */
public class Predictor {
    private ProbabilitiesDatabase probs;
    private String alphabet = "abcdefghijklmnopqrstuvwxyz -'";
    private int n;

    public Predictor(int n) {
        this.probs = new ProbabilitiesDatabase(n);
        this.n = n;
        KeyboardMessageDisplay kb = new KeyboardMessageDisplay();
        kb.showHeart(true);
        probs.loadTextFile("big.txt");
        probs.loadTextFile("words2.txt");
        kb.showHeart(false);
//		LogiLED.LogiLedSetLighting(0, 0, 0);
    }

    public boolean isValidChar(char c) {
        return alphabet.contains("" + c);
    }

    public double computeProbability(String prev, char next) {
        double denom = probs.getNgramProbabilities(prev);
        if (denom <= 0) {
            return 0;
        }
        return probs.getNgramProbabilities(prev + next)/denom;
    }

    public List<CharProbPair> getNextChar(String text) {
        List<CharProbPair> letters = new ArrayList<>();

        String last = text;

        letters.clear();
        for (char c : alphabet.toCharArray()) {
            letters.add(new CharProbPair(c, computeProbability(last, c)));
        }
        Collections.sort(letters);

        // No prediction ? Only show space
        if (letters.get(0).getProbability() == 0) {
            letters.clear();
            letters.add(new CharProbPair(' ', 1.0));
        }

        return letters;
    }
}
