package com.machina.minterfacebuilder.helpers;

import com.machina.minterfacebuilder.model.LiteralValue;

public class Color extends LiteralValue {
    /**
     * Create a color literal value.
     * @param color The color string.
     * @return A LiteralValue instance.
     */
    public static final Color of(String color) {
        if (!color.startsWith("#")) {
            throw new IllegalArgumentException("Color must start with #");
        }

        return new Color(color);
    }

    /**
     * Create a color literal value.
     * @param color The color string.
     * @param alpha The alpha value.
     * @return A LiteralValue instance.
     */
    public static final Color of(String color, double alpha) {
        return of(color + "(" + alpha + ")");
    }

    protected Color(String color) {
        super(color);
    }
}
