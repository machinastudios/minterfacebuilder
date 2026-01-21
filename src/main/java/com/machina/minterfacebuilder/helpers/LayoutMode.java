package com.machina.minterfacebuilder.helpers;

import com.machina.minterfacebuilder.model.LiteralValue;

public class LayoutMode extends LiteralValue {
    public static final LayoutMode TOP = new LayoutMode("Top");
    public static final LayoutMode MIDDLE = new LayoutMode("Middle");
    public static final LayoutMode CENTER = new LayoutMode("Center");
    public static final LayoutMode FULL = new LayoutMode("Full");
    public static final LayoutMode CENTER_MIDDLE = new LayoutMode("CenterMiddle");
    public static final LayoutMode MIDDLE_CENTER = new LayoutMode("MiddleCenter");
    public static final LayoutMode TOP_SCROLLING = new LayoutMode("TopScrolling");
    public static final LayoutMode LEFT_CENTER_WRAP = new LayoutMode("LeftCenterWrap");

    /**
     * Get the layout mode literal value for the given layout mode string.
     * @param layoutMode The layout mode string.
     * @return The layout mode literal value.
     */
    public static LayoutMode of(String layoutMode) {
        return new LayoutMode(layoutMode);
    }

    protected LayoutMode(String value) {
        super(value);
    }
}
