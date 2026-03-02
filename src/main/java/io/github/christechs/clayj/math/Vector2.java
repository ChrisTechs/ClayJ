package io.github.christechs.clayj.math;

public class Vector2 {
    public float x;
    public float y;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        set(x, y);
    }

    public void set(Vector2 other) {
        set(other.x, other.y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}