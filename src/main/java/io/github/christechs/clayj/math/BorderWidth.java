package io.github.christechs.clayj.math;

public class BorderWidth {
    public int left;
    public int right;
    public int top;
    public int bottom;
    public int betweenChildren;

    public BorderWidth() {
    }

    public BorderWidth(int left, int right, int top, int bottom, int betweenChildren) {
        set(left, right, top, bottom, betweenChildren);
    }

    public void set(BorderWidth other) {
        set(other.left, other.right, other.top, other.bottom, other.betweenChildren);
    }

    public void set(int left, int right, int top, int bottom, int betweenChildren) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.betweenChildren = betweenChildren;
    }
}