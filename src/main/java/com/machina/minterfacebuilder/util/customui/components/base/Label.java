package com.machina.minterfacebuilder.util.customui.components.base;

import java.util.Map;

import javax.annotation.Nullable;

public class Label extends TextableComponent {
    public Label() {
        this(Map.of());
    }

    public Label(@Nullable String text) {
        this(Map.of("text", text));
    }

    public Label(@Nullable Map<String, String> attributes) {
        super("Label");

        // Set text
        if (attributes != null && attributes.containsKey("text")) {
            setProperty("Text", attributes.get("text"));
        }
    }
}
