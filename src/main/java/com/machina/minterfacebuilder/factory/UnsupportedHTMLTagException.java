package com.machina.minterfacebuilder.factory;

/**
 * Exception thrown when an unsupported HTML tag is encountered during parsing.
 */
public class UnsupportedHTMLTagException extends RuntimeException {
    /**
     * The unsupported HTML tag name.
     */
    private final String tagName;

    /**
     * Constructor.
     * @param tagName The unsupported HTML tag name.
     */
    public UnsupportedHTMLTagException(String tagName) {
        super("Unsupported HTML tag: <" + tagName + ">. This tag is not supported by Hytale Custom UI system.");
        this.tagName = tagName;
    }

    /**
     * Get the unsupported HTML tag name.
     * @return The tag name.
     */
    public String getTagName() {
        return tagName;
    }
}
