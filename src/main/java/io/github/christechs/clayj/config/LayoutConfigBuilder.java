package io.github.christechs.clayj.config;

import io.github.christechs.clayj.enums.LayoutAlignmentX;
import io.github.christechs.clayj.enums.LayoutAlignmentY;
import io.github.christechs.clayj.enums.LayoutDirection;
import io.github.christechs.clayj.enums.SizingType;
import io.github.christechs.clayj.math.Padding;
import io.github.christechs.clayj.math.Sizing;
import io.github.christechs.clayj.math.SizingAxis;

public final class LayoutConfigBuilder implements ConfigBuilder {

    public final Sizing sizing = new Sizing();
    public final Padding padding = new Padding();

    public int childGap = 0;
    public LayoutAlignmentX alignX = LayoutAlignmentX.LEFT;
    public LayoutAlignmentY alignY = LayoutAlignmentY.TOP;
    public LayoutDirection direction = LayoutDirection.LEFT_TO_RIGHT;

    public LayoutConfigBuilder() {
    }

    public void set(LayoutConfigBuilder other) {
        this.sizing.set(other.sizing);
        this.padding.set(other.padding);
        this.childGap = other.childGap;
        this.alignX = other.alignX;
        this.alignY = other.alignY;
        this.direction = other.direction;
    }

    public LayoutConfigBuilder sizing(SizingAxis width, SizingAxis height) {
        sizing.set(width, height);
        return this;
    }

    public LayoutConfigBuilder sizing(SizingType wType, float wVal, SizingType hType, float hVal) {
        sizing.width.type = wType;
        if (wType == SizingType.PERCENT) {
            sizing.width.percent = wVal;
        } else if (wType == SizingType.GROW) {
            sizing.width.minMax.min = 0;
            sizing.width.minMax.max = wVal;
        } else {
            sizing.width.minMax.min = wVal;
            sizing.width.minMax.max = wVal;
        }

        sizing.height.type = hType;
        if (hType == SizingType.PERCENT) {
            sizing.height.percent = hVal;
        } else if (hType == SizingType.GROW) {
            sizing.height.minMax.min = 0;
            sizing.height.minMax.max = hVal;
        } else {
            sizing.height.minMax.min = hVal;
            sizing.height.minMax.max = hVal;
        }
        return this;
    }

    public LayoutConfigBuilder padding(int all) {
        this.padding.left = all;
        this.padding.right = all;
        this.padding.top = all;
        this.padding.bottom = all;
        return this;
    }

    public LayoutConfigBuilder padding(int x, int y) {
        this.padding.left = x;
        this.padding.right = x;
        this.padding.top = y;
        this.padding.bottom = y;
        return this;
    }

    public LayoutConfigBuilder gap(int gap) {
        this.childGap = gap;
        return this;
    }

    public LayoutConfigBuilder align(LayoutAlignmentX x, LayoutAlignmentY y) {
        this.alignX = x;
        this.alignY = y;
        return this;
    }

    public LayoutConfigBuilder dir(LayoutDirection direction) {
        this.direction = direction;
        return this;
    }

    public void reset() {
        sizing.width.type = SizingType.FIT;
        sizing.width.percent = 0f;
        sizing.width.minMax.min = 0f;
        sizing.width.minMax.max = 0f;

        sizing.height.type = SizingType.FIT;
        sizing.height.percent = 0f;
        sizing.height.minMax.min = 0f;
        sizing.height.minMax.max = 0f;

        padding.left = 0;
        padding.right = 0;
        padding.top = 0;
        padding.bottom = 0;

        childGap = 0;
        alignX = LayoutAlignmentX.LEFT;
        alignY = LayoutAlignmentY.TOP;
        direction = LayoutDirection.LEFT_TO_RIGHT;
    }
}