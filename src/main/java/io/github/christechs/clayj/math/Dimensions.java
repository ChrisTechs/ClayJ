package io.github.christechs.clayj.math;

public class Dimensions {
    public float width;
    public float height;

    public Dimensions() {
    }

    public Dimensions(float width, float height) {
        set(width, height);
    }

    public void set(Dimensions other) {
        set(other.width, other.height);
    }

    public void set(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float sizeAxis(boolean xAxis) {
        return xAxis ? width : height;
    }

    public float aspect() {
        return width / height;
    }
}