package com.machina.minterfacebuilder.util.customui.components.base;

import java.util.Map;

import javax.annotation.Nullable;

public class Image extends Group {
    /**
     * The source of the image.
     */
    private String src;

    public Image() {
        this(Map.of());
    }

    public Image(@Nullable String src) {
        this();
        setSrc(src);
    }

    public Image(@Nullable Map<String, String> attributes) {
        super();
        setSrc(attributes.get("src"));
    }

    /**
     * Set the source of the image.
     * @param src The source of the image.
     * @return The image.
     */
    public Image setSrc(@Nullable String src) {
        this.src = src;
        return this;
    }

    /**
     * Set the sizes of the image.
     * @param sizes The sizes of the image.
     * @return The image.
     */
    public Image setSizes(int sizes) {
        return setSizes(sizes, sizes);
    }

    /**
     * Set the sizes of the image.
     * @param width The width of the image.
     * @param height The height of the image.
     * @return The image.
     */
    public Image setSizes(int width, int height) {
        setProperty("Anchor", Map.of("Width", width, "Height", height));
        return this;
    }

    public String build() {
        // Set the background attribute
        setProperty("Background", src);

        return super.build();
    }
}
