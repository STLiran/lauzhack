package ch.lauzhack;

/**
 * Created by Loic on 19.11.2016.
 */
public class CharProbPair implements Comparable<CharProbPair> {
    private char c;
    private double p;

    public CharProbPair(char c, double p) {
        this.c = c;
        this.p = p;
    }

    public char getChar() {
        return c;
    }

    public double getProbability() {
        return p;
    }

    @Override
    public int compareTo(CharProbPair that) {
        if (this.p < that.p)
            return 1;
        else if (this.p > that.p)
            return -1;
        return 0;
    }
}
