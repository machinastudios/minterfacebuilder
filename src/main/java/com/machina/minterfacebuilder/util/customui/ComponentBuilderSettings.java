package com.machina.minterfacebuilder.util.customui;

public class ComponentBuilderSettings {
    /**
     * Whether to use minimal formatting for the component.
     */
    public boolean minimal = false;
    
    /**
     * Constructor for the component builder settings with default values.
     */
    public ComponentBuilderSettings() { }

    /**
     * Constructor for the component builder settings with the given settings.
     * @param minimal Whether to use minimal formatting for the component.
     */
    public ComponentBuilderSettings(boolean minimal) {
        this.minimal = minimal;
    }

    /**
     * Constructor for the component builder settings with the given settings.
     * @param minimal Whether to use minimal formatting for the component.
     * @param indentLevel The indent level for the component.
     */
    public ComponentBuilderSettings(boolean minimal, int indentLevel) {
        this.minimal = minimal;
    }
}
