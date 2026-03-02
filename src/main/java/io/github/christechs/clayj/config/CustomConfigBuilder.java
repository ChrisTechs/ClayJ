package io.github.christechs.clayj.config;

public final class CustomConfigBuilder implements ConfigBuilder {
    public Object customData;

    public void set(CustomConfigBuilder other) {
        this.customData = other.customData;
    }

    public CustomConfigBuilder customData(Object customData) {
        this.customData = customData;
        return this;
    }

    public void reset() {
        customData = null;
    }
}