/*
 * Original Clay Library Copyright (c) 2024 Nic Barker
 * Licensed under the zlib/libpng license.
 *
 * See the LICENSE.md file in the root of this repository for the
 * full zlib/libpng license text.
 *
 * Note: This source file has been altered from the original Clay
 * distribution. The modifications are released into the public domain.
 */
package io.github.christechs.clayj.core;

import io.github.christechs.clayj.ClayJ;
import io.github.christechs.clayj.ClayJContext;
import io.github.christechs.clayj.config.*;
import io.github.christechs.clayj.enums.ClayJError;
import io.github.christechs.clayj.enums.ElementConfigType;
import io.github.christechs.clayj.math.Dimensions;

public final class LayoutElement {

    public Dimensions minDimensions = new Dimensions(0f, 0f);
    public Dimensions dimensions = new Dimensions(0f, 0f);

    public int childrenStart = -1;
    public int childrenLength = 0;
    public int floatingChildrenCount = 0;

    public boolean isTextElement = false;
    public int textElementDataIndex = -1;
    public int id = 0;

    public LayoutConfigBuilder layoutConfig;
    public SharedConfigBuilder sharedConfig;
    public TextConfigBuilder textConfig;
    public ImageConfigBuilder imageConfig;
    public FloatingConfigBuilder floatingConfig;
    public ScrollConfigBuilder scrollConfig;
    public BorderConfigBuilder borderConfig;
    public CustomConfigBuilder customConfig;

    public LayoutElement() {
    }

    public void set(LayoutElement other) {
        this.minDimensions = other.minDimensions;
        this.dimensions = other.dimensions;
        this.childrenStart = other.childrenStart;
        this.childrenLength = other.childrenLength;
        this.floatingChildrenCount = other.floatingChildrenCount;
        this.isTextElement = other.isTextElement;
        this.textElementDataIndex = other.textElementDataIndex;
        this.id = other.id;

        this.layoutConfig = other.layoutConfig;
        this.sharedConfig = other.sharedConfig;
        this.textConfig = other.textConfig;
        this.imageConfig = other.imageConfig;
        this.floatingConfig = other.floatingConfig;
        this.scrollConfig = other.scrollConfig;
        this.borderConfig = other.borderConfig;
        this.customConfig = other.customConfig;
    }

    public ConfigBuilder getConfig(ElementConfigType type) {
        return switch (type) {
            case SHARED -> sharedConfig;
            case TEXT -> textConfig;
            case IMAGE -> imageConfig;
            case FLOATING -> floatingConfig;
            case SCROLL -> scrollConfig;
            case BORDER -> borderConfig;
            case CUSTOM -> customConfig;
            default -> null;
        };
    }

    public void attachConfig(ElementConfigType type, ConfigBuilder config) {
        ClayJContext context = ClayJ.getContext();
        if (getConfig(type) != null) {
            if (context != null && context.errorHandler != null) {
                context.errorHandler.handleError(ClayJError.DUPLICATE_CONFIG);
            }
            return;
        }

        switch (type) {
            case SHARED -> this.sharedConfig = (SharedConfigBuilder) config;
            case TEXT -> this.textConfig = (TextConfigBuilder) config;
            case IMAGE -> this.imageConfig = (ImageConfigBuilder) config;
            case FLOATING -> this.floatingConfig = (FloatingConfigBuilder) config;
            case SCROLL -> this.scrollConfig = (ScrollConfigBuilder) config;
            case BORDER -> this.borderConfig = (BorderConfigBuilder) config;
            case CUSTOM -> this.customConfig = (CustomConfigBuilder) config;
        }
    }

    public void reset() {
        childrenStart = -1;
        childrenLength = 0;
        floatingChildrenCount = 0;
        isTextElement = false;
        textElementDataIndex = -1;
        id = 0;

        dimensions = new Dimensions(0f, 0f);
        minDimensions = new Dimensions(0f, 0f);

        layoutConfig = null;
        sharedConfig = null;
        textConfig = null;
        imageConfig = null;
        floatingConfig = null;
        scrollConfig = null;
        borderConfig = null;
        customConfig = null;
    }
}