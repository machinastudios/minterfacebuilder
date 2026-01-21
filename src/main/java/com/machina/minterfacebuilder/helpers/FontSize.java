package com.machina.minterfacebuilder.helpers;

import com.machina.minterfacebuilder.model.LiteralValue;

public class FontSize extends LiteralValue {
    /**
     * Create a font size literal.
     * @param size The font size.
     * @return An InterfaceLiteral instance.
     */
    public static FontSize of(int size) {
        return new FontSize(size);
    }

    protected FontSize(int size) {
        super(String.valueOf(size));
    }
}
