package io.github.christechs.clayj.config;

import io.github.christechs.clayj.math.Dimensions;

public final class ImageConfigBuilder implements ConfigBuilder {
    public Object imageData;
    public Dimensions sourceDimensions = new Dimensions();

    public ImageConfigBuilder data(Object data) {
        this.imageData = data;
        return this;
    }

    public ImageConfigBuilder sourceDim(float width, float height) {
        this.sourceDimensions.width = width;
        this.sourceDimensions.height = height;
        return this;
    }

    public void set(ImageConfigBuilder other) {
        set(other.imageData, other.sourceDimensions);
    }

    public void set(Object imageData, Dimensions sourceDimensions) {
        this.imageData = imageData;
        this.sourceDimensions.set(sourceDimensions);
    }

    public void reset() {
        this.imageData = null;
        this.sourceDimensions.width = 0;
        this.sourceDimensions.height = 0;
    }
}