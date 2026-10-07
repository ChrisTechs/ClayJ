/*
 * This is free and unencumbered software released into the public domain.
 *
 * See the LICENSE.md file for more information, or visit <https://unlicense.org/>
 */
package io.github.christechs.clayj.math;

public record BoundingBox(float x, float y, float width, float height) {

    public BoundingBox set(BoundingBox other) {
        return new BoundingBox(other.x(), other.y(), other.width(), other.height());
    }

    public BoundingBox set(float x, float y, float width, float height) {
        return new BoundingBox(x, y, width, height);
    }

    public boolean contains(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public boolean contains(Vector2 point) {
        return contains(point.x(), point.y());
    }

    public boolean intersects(BoundingBox other) {
        return this.x < other.x() + other.width() &&
                this.x + this.width > other.x() &&
                this.y < other.y() + other.height() &&
                this.y + this.height > other.y();
    }
}