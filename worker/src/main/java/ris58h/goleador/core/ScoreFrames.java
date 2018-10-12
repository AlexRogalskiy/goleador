package ris58h.goleador.core;

import java.util.Objects;

public class ScoreFrames {
    public final Score score;
    public final int first;
    public int last; // it's not final to simplify score reducer

    public ScoreFrames(Score score, int first, int last) {
        this.score = score;
        this.first = first;
        this.last = last;
    }

    @Override
    public String toString() {
        return score.toString() + ':' + first + ':' + last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreFrames that = (ScoreFrames) o;
        return first == that.first &&
                last == that.last &&
                Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, first, last);
    }

    public static ScoreFrames parse(String s) {
        String[] split = s.split(":");
        Score score = Score.parseScore(split[0]);
        int first = Integer.parseInt(split[1]);
        int last = Integer.parseInt(split[2]);
        return new ScoreFrames(score, first, last);
    }
}
