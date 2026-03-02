package io.github.christechs.clayj.math;

public class SizingMinMax {
    public float min;
    public float max;

    public SizingMinMax() {
    }

    public SizingMinMax(float min, float max) {
        set(min, max);
    }

    public void set(SizingMinMax other) {
        set(other.min, other.max);
    }

    public void set(float min, float max) {
        this.min = min;
        this.max = max;
    }
}