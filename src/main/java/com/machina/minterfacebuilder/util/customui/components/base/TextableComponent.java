package com.machina.minterfacebuilder.util.customui.components.base;

import com.machina.minterfacebuilder.helpers.TranslationKey;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

public class TextableComponent extends ComponentBuilder {
    protected TextableComponent(String component) {
        super(component);
    }

    /**
     * Set the text for the component.
     * @param text The text to set.
     * @return The builder instance.
     */
    public TextableComponent setText(Object text) {
        // If the text is null, throw an exception
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        // If it's a string
        if (text instanceof String) {
            // If the text starts with a %, it's a translation key
            if (((String) text).startsWith("%")) {
                text = TranslationKey.of((String) text);
            }
        }

        this.setProperty("Text", text);
        return this;
    }
}
