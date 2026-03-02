package io.github.christechs.clayj.math;

public class BoundingBox {
    public float x;
    public float y;
    public float width;
    public float height;

    public BoundingBox() {
    }

    public BoundingBox(float x, float y, float width, float height) {
        set(x, y, width, height);
    }

    public void set(BoundingBox other) {
        set(other.x, other.y, other.width, other.height);
    }

    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}