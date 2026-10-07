/*
 * This is free and unencumbered software released into the public domain.
 *
 * See the LICENSE.md file for more information, or visit <https://unlicense.org/>
 */
package io.github.christechs.clayj.math;

public record Sizing(SizingAxis width, SizingAxis height) {

    public Sizing set(Sizing other) {
        return new Sizing(other.width(), other.height());
    }

    public Sizing set(SizingAxis width, SizingAxis height) {
        return new Sizing(width, height);
    }

    public SizingAxis sizingAxis(boolean xAxis) {
        return xAxis ? width : height;
    }

    public Dimensions clamp(Dimensions d) {
        return new Dimensions(clampWidth(d.width()), clampHeight(d.height()));
    }

    public float clampWidth(float w) {
        return Math.clamp(width.minMax().max(), width.minMax().min(), w);
    }

    public float clampHeight(float h) {
        return Math.clamp(height.minMax().max(), height.minMax().min(), h);
    }
}