package com.machina.minterfacebuilder.util.customui;

public class ComponentBuilderSettings {
    /**
     * Whether to use minimal formatting for the component.
     */
    public boolean minimal = false;
    
    /**
     * Whether to skip generating @MIB variable definitions.
     * Useful when the parent class already generates them with custom styles.
     */
    public boolean skipMIVariables = false;
    
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
     * @param skipMIVariables Whether to skip generating @MIB variable definitions.
     */
    public ComponentBuilderSettings(boolean minimal, boolean skipMIVariables) {
        this.minimal = minimal;
        this.skipMIVariables = skipMIVariables;
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
