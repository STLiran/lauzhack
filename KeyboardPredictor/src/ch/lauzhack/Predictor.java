package ch.lauzhack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        probs.loadTextFile("big.txt");
        probs.loadOrCreate("big.txt", "save.txt");
    }

    public double computeProbability(String prev, char next) {
        return probs.getNgramProbabilities(prev + next)/probs.getNgramProbabilities(prev);
    }

    public List<CharProbPair> getNextChar(String text) {
    /* Not enough chars to do a prediction */
        if (text.length() < 1) {
            return Collections.emptyList();
        }

        String last = text.substring(Math.max(0, text.length() - (n - 1)), text.length());

        List<CharProbPair> letters = new ArrayList<>();
        for (char c : alphabet.toCharArray()) {
            letters.add(new CharProbPair(c, computeProbability(last, c)));
        }
        Collections.sort(letters);

        return letters;
    }
}
