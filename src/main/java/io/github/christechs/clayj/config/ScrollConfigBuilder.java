package io.github.christechs.clayj.config;

public final class ScrollConfigBuilder implements ConfigBuilder {
    public boolean horizontal = false;
    public boolean vertical = false;

    public void set(ScrollConfigBuilder other) {
        this.horizontal = other.horizontal;
        this.vertical = other.vertical;
    }

    public ScrollConfigBuilder horizontal(boolean scroll) {
        this.horizontal = scroll;
        return this;
    }

    public ScrollConfigBuilder vertical(boolean scroll) {
        this.vertical = scroll;
        return this;
    }

    public ScrollConfigBuilder both() {
        this.horizontal = true;
        this.vertical = true;
        return this;
    }

    public void reset() {
        this.horizontal = false;
        this.vertical = false;
    }
}