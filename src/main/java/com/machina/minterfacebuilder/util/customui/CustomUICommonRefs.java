package com.machina.minterfacebuilder.util.customui;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.ui.Value;

public class CustomUICommonRefs {
    /**
     * Returns a reference to the default text button style
     */
    public static final @Nonnull Value<Object> DefaultTextButtonStyle() {
        return Value.ref("Common.ui", "DefaultTextButtonStyle");
    }

    /**
     * Returns a reference to the secondary text button style
     */
    public static final @Nonnull Value<Object> SecondaryTextButtonStyle() {
        return Value.ref("Common.ui", "SecondaryTextButtonStyle");
    }
}
