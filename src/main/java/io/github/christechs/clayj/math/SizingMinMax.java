/*
 * This is free and unencumbered software released into the public domain.
 *
 * See the LICENSE.md file for more information, or visit <https://unlicense.org/>
 */
package io.github.christechs.clayj.math;

public record SizingMinMax(float min, float max) {

    public SizingMinMax set(SizingMinMax other) {
        return new SizingMinMax(other.min(), other.max());
    }

    public SizingMinMax set(float min, float max) {
        return new SizingMinMax(min, max);
    }

    public SizingMinMax min(float min) {
        return new SizingMinMax(min, this.max);
    }

    public SizingMinMax max(float max) {
        return new SizingMinMax(this.min, max);
    }
}