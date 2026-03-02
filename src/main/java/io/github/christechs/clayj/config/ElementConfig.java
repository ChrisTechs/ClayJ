package io.github.christechs.clayj.config;

import io.github.christechs.clayj.enums.ElementConfigType;

public class ElementConfig {
    public ElementConfigType type = ElementConfigType.NONE;
    public ConfigBuilder config;

    public void set(ElementConfig other) {
        this.type = other.type;
        this.config = other.config;
    }

    public void reset() {
        type = ElementConfigType.NONE;
        config = null;
    }
}