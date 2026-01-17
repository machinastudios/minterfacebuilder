package com.machina.minterfacebuilder.util.customui;

public abstract class HytaleCustomUIComponent extends ComponentBuilder {
    /**
     * The tag name of the component.
     */
    private static String TAG_NAME;

    /**
     * Get the tag name of the component.
     * @return The tag name of the component.
     */
    public static String getTagName() {
        return TAG_NAME;
    }

    protected HytaleCustomUIComponent(String component) {
        super(component);
    }
}
