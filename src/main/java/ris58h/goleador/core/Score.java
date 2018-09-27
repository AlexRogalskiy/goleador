package ris58h.goleador.core;

import java.util.Objects;

public class Score {

    private static int CACHE_DIMENSION = 10;
    private static Score[] CACHE = new Score[CACHE_DIMENSION * CACHE_DIMENSION ];
    static {
        for (int left = 0; left < CACHE_DIMENSION; left++) {
            for (int right = 0; right < CACHE_DIMENSION; right++) {
                CACHE[left * CACHE_DIMENSION + right] = new Score(left, right);
            }
        }
    }

    public final int left;
    public final int right;

    private Score(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public static Score of(int left, int right) {
        if (left < CACHE_DIMENSION && right < CACHE_DIMENSION) {
            return CACHE[left * CACHE_DIMENSION + right];
        }
        return new Score(left, right);
    }

    @Override
    public String toString() {
        return this.left + "-" + this.right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return left == score.left &&
                right == score.right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public static Score parseScore(String s) {
        String[] split = s.split("-");
        return Score.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
