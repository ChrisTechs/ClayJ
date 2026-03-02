package io.github.christechs.clayj.math;

public class Color {
    public float r, g, b, a;

    public Color() {
    }

    public Color(float r, float g, float b, float a) {
        set(r, g, b, a);
    }

    public void set(Color other) {
        set(other.r, other.g, other.b, other.a);
    }

    public void set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}