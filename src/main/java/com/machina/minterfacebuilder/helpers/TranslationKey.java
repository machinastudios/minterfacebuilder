package com.machina.minterfacebuilder.helpers;

import javax.annotation.Nonnull;

import com.machina.minterfacebuilder.model.LiteralValue;

public class TranslationKey extends LiteralValue {
    /**
     * Create a translation key.
     * @param value The translation key.
     * @return A TranslationKey instance.
     */
    @Nonnull
    public static final TranslationKey of(String value) {
        // If the value doesn't start with a %, add it
        if (!value.startsWith("%")) {
            value = "%" + value;
        }

        return new TranslationKey(value);
    }

    protected TranslationKey(String value) {
        super(value);
    }

    /**
     * Get the translation key as a string.
     * @return The translation key as a string.
     */
    public String getValue() {
        return super.getValue();
    }
}
