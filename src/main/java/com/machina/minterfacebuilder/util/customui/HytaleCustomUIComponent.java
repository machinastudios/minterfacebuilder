package com.machina.minterfacebuilder.util.customui;

public abstract class HytaleCustomUIComponent extends ComponentBuilder {
    /**
     * The tag name of the component.
     * SHOULD be overridden by the subclass.
     */
    private static String TAG_NAME;

    /**
     * Construct the component.
     */
    public HytaleCustomUIComponent() {
        super(TAG_NAME);
        throw new IllegalStateException("HytaleCustomUIComponent needs to support a public constructor without parameters");
    }

    protected HytaleCustomUIComponent(String component) {
        super(component);
    }
}
