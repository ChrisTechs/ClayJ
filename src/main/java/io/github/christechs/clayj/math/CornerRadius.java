package io.github.christechs.clayj.math;

public class CornerRadius {
    public float topLeft;
    public float topRight;
    public float bottomLeft;
    public float bottomRight;

    public CornerRadius() {
    }

    public CornerRadius(float radius) {
        set(radius, radius, radius, radius);
    }

    public CornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        set(topLeft, topRight, bottomLeft, bottomRight);
    }

    public void set(CornerRadius other) {
        set(other.topLeft, other.topRight, other.bottomLeft, other.bottomRight);
    }

    public void set(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}