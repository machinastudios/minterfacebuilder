package com.machina.minterfacebuilder.helpers;

import com.machina.minterfacebuilder.model.LiteralValue;

public class Alignment extends LiteralValue {
    public static final Alignment START = new Alignment("Start");
    public static final Alignment END = new Alignment("End");
    public static final Alignment CENTER = new Alignment("Center");
    public static final Alignment RIGHT = new Alignment("Right");
    public static final Alignment LEFT = new Alignment("Left");
    public static final Alignment TOP = new Alignment("Top");
    public static final Alignment BOTTOM = new Alignment("Bottom");

    /**
     * Get the alignment literal value for the given alignment string.
     * @param alignment The alignment string.
     * @return The alignment literal value.
     */
    public static Alignment of(String alignment) {
        return new Alignment(alignment);
    }

    protected Alignment(String value) {
        super(value);
    }
}
