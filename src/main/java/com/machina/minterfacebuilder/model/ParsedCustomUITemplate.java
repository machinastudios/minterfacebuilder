package com.machina.minterfacebuilder.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.machina.minterfacebuilder.model.InterfaceVariable;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.ComponentBuilderSettings;
import com.machina.minterfacebuilder.util.customui.CustomUIComponentProperty;

/**
 * Represents a parsed Custom UI template with support for variables.
 * Extends ComponentBuilder for easier usage.
 */
public class ParsedCustomUITemplate extends ComponentBuilder {
    /**
     * The ComponentBuilder containing the UI structure.
     */
    private final ComponentBuilder componentBuilder;

    /**
     * The variables defined in the template (e.g., @Valor = "Sim").
     */
    private final Map<String, InterfaceVariable> variables;

    /**
     * The HTML tags used in the template that need aliases (e.g., h1, h2, span, p, label).
     */
    private final Set<String> usedHtmlTags;

    /**
     * Create a new ParsedCustomUITemplate.
     * @param componentBuilder The ComponentBuilder.
     * @param variables The variables map (InterfaceVariable instances).
     */
    public ParsedCustomUITemplate(ComponentBuilder componentBuilder, Map<String, InterfaceVariable> variables) {
        this(componentBuilder, variables, new HashSet<>());
    }

    /**
     * Create a new ParsedCustomUITemplate.
     * @param componentBuilder The ComponentBuilder.
     * @param variables The variables map (InterfaceVariable instances).
     * @param usedHtmlTags The HTML tags used in the template that need aliases.
     */
    public ParsedCustomUITemplate(ComponentBuilder componentBuilder, Map<String, InterfaceVariable> variables, Set<String> usedHtmlTags) {
        super("Group");
        this.componentBuilder = componentBuilder;
        this.variables = new HashMap<>(variables);
        this.usedHtmlTags = new HashSet<>(usedHtmlTags != null ? usedHtmlTags : new HashSet<>());
    }

    /**
     * Get the variables map.
     * @return The variables map (InterfaceVariable instances).
     */
    public Map<String, InterfaceVariable> getVariables() {
        return new HashMap<>(this.variables);
    }

    /**
     * Set a variable value (template variable, not ComponentBuilder variable).
     * @param name The variable name (without @ prefix).
     * @param value The variable value (String - will be converted to InterfaceVariable.stringValue).
     * @return This instance for chaining.
     */
    public ParsedCustomUITemplate setVariable(String name, String value) {
        // Remove @ prefix if present
        String varName = name.startsWith("@") ? name.substring(1) : name;
        this.variables.put(varName, InterfaceVariable.stringValue(value));
        return this;
    }

    /**
     * Set a variable value with type (template variable, not ComponentBuilder variable).
     * @param name The variable name (without @ prefix).
     * @param variable The InterfaceVariable instance.
     * @return This instance for chaining.
     */
    public ParsedCustomUITemplate setVariable(String name, InterfaceVariable variable) {
        // Remove @ prefix if present
        String varName = name.startsWith("@") ? name.substring(1) : name;
        this.variables.put(varName, variable);
        return this;
    }

    /**
     * Set multiple variable values.
     * @param variables The variables map (String values - will be converted to InterfaceVariable.stringValue).
     */
    public void setVariables(Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            this.setVariable(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set multiple variable values with types.
     * @param variables The variables map (InterfaceVariable instances).
     */
    public void setVariablesTyped(Map<String, InterfaceVariable> variables) {
        for (Map.Entry<String, InterfaceVariable> entry : variables.entrySet()) {
            this.setVariable(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Get a variable value as string.
     * @param name The variable name (without @ prefix).
     * @return The variable value as string, or null if not found.
     */
    public String getVariable(String name) {
        // Remove @ prefix if present
        String varName = name.startsWith("@") ? name.substring(1) : name;
        InterfaceVariable var = this.variables.get(varName);
        return var != null ? var.getValue() : null;
    }

    /**
     * Get a variable with type information.
     * @param name The variable name (without @ prefix).
     * @return The InterfaceVariable instance, or null if not found.
     */
    public InterfaceVariable getVariableTyped(String name) {
        // Remove @ prefix if present
        String varName = name.startsWith("@") ? name.substring(1) : name;
        return this.variables.get(varName);
    }

    /**
     * Build the UICommandBuilder with variable substitutions applied.
     * Note: Variables are substituted during parsing, so this method returns a new UICommandBuilder.
     * If you modify variables after parsing using setVariable(), you can apply them to components
     * using the returned UICommandBuilder.
     * @return The UICommandBuilder.
     */
    public UICommandBuilder buildUICommandBuilder() {
        UICommandBuilder commandBuilder = new UICommandBuilder();

        // The variables have already been substituted during parsing.
        // If you need to apply variable updates after parsing, you can do so using
        // commandBuilder.set() with selectors for specific components.

        return commandBuilder;
    }

    @Override
    public String build(ComponentBuilderSettings settings) {
        return buildWithAliasesAndVariables(settings);
    }

    @Override
    public String build() {
        return buildWithAliasesAndVariables(null);
    }

    /**
     * Build the UI with aliases and variables at the top.
     * @param settings The settings to use (can be null).
     * @return The UI as a string with aliases and variables at the top.
     */
    private String buildWithAliasesAndVariables(ComponentBuilderSettings settings) {
        StringBuilder output = new StringBuilder();

        // Build aliases for HTML tags that map to Label (h1-h6, span, p, label)
        // Format: @_MI_h1 = Label { Style: (...) }
        Map<String, String> tagToComponentMap = new HashMap<>();
        tagToComponentMap.put("h1", "Label");
        tagToComponentMap.put("h2", "Label");
        tagToComponentMap.put("h3", "Label");
        tagToComponentMap.put("h4", "Label");
        tagToComponentMap.put("h5", "Label");
        tagToComponentMap.put("h6", "Label");
        tagToComponentMap.put("span", "Label");
        tagToComponentMap.put("p", "Label");
        tagToComponentMap.put("label", "Label");

        // Default font sizes for headings (h1 = largest, h6 = smallest)
        Map<String, Integer> headingFontSizes = new HashMap<>();
        headingFontSizes.put("h1", 28);
        headingFontSizes.put("h2", 24);
        headingFontSizes.put("h3", 20);
        headingFontSizes.put("h4", 18);
        headingFontSizes.put("h5", 16);
        headingFontSizes.put("h6", 14);

        // Generate aliases for used HTML tags
        for (String tag : this.usedHtmlTags) {
            String component = tagToComponentMap.get(tag.toLowerCase());
            if (component != null) {
                output.append("@_MI_").append(tag.toLowerCase()).append(" = ").append(component).append(" {\n");
                
                // Add default styles for headings (h1-h6)
                if (tag.matches("h[1-6]")) {
                    Integer fontSize = headingFontSizes.get(tag.toLowerCase());
                    if (fontSize != null) {
                        output.append("  Style: (\n");
                        output.append("    FontSize: ").append(fontSize).append(",\n");
                        output.append("    RenderBold: true\n");
                        output.append("  );\n");
                    }
                } else if (tag.equalsIgnoreCase("p")) {
                    // Add default margin-bottom for paragraphs
                    output.append("  Anchor: (\n");
                    output.append("    Bottom: 8\n");
                    output.append("  );\n");
                }
                
                output.append("}\n\n");
            }
        }

        // Build variables from script block (format: @VariableName = value;)
        if (!this.variables.isEmpty()) {
            for (Map.Entry<String, InterfaceVariable> entry : this.variables.entrySet()) {
                String varName = entry.getKey();
                InterfaceVariable var = entry.getValue();
                String varValue = var.getValue();
                
                // Format the value (add quotes if it's a string literal)
                String formattedValue = formatVariableValue(varValue, var.getType());
                
                output.append("@").append(varName).append(" = ").append(formattedValue).append(";\n");
            }
            output.append("\n");
        }

        // Build the component structure
        String componentOutput = settings != null ? 
            this.componentBuilder.build(settings) : 
            this.componentBuilder.build();
        
        output.append(componentOutput);
        
        return output.toString();
    }

    /**
     * Format a variable value for output.
     * @param value The variable value.
     * @param type The variable type.
     * @return The formatted value string.
     */
    private String formatVariableValue(String value, InterfaceVariable.Type type) {
        if (value == null) {
            return "null";
        }

        // Booleans and literals (which may contain numbers) don't need quotes
        if (type == InterfaceVariable.Type.BOOLEAN || 
            type == InterfaceVariable.Type.LITERAL) {
            return value;
        }

        // Strings need quotes (unless they're already quoted)
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value;
        }
        if (value.startsWith("'") && value.endsWith("'")) {
            return value;
        }

        return "\"" + value.replace("\"", "\\\"") + "\"";
    }

    @Override
    public ComponentBuilder appendChild(String child) {
        return this.componentBuilder.appendChild(child);
    }

    @Override
    public ComponentBuilder appendChild(ComponentBuilder child) {
        return this.componentBuilder.appendChild(child);
    }

    @Override
    public ComponentBuilder setSettings(ComponentBuilderSettings settings) {
        return this.componentBuilder.setSettings(settings);
    }

    // Note: setVariable() is not overridden here because we have our own setVariable() method
    // that modifies template variables. Use the ComponentBuilder's setVariable via delegation if needed.
    // This class's setVariable() modifies the template variables map.

    @Override
    public ComponentBuilder setStyle(String style, String value) {
        return this.componentBuilder.setStyle(style, value);
    }

    @Override
    public ComponentBuilder setStyle(Map<String, String> styles) {
        return this.componentBuilder.setStyle(styles);
    }

    @Override
    public ComponentBuilder setId(String id) {
        return this.componentBuilder.setId(id);
    }

    @Override
    public <T> T getProperty(String property) {
        return this.componentBuilder.getProperty(property);
    }

    @Override
    public ComponentBuilder setProperty(String property, Object value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperties(Map<String, Object> properties) {
        return this.componentBuilder.setProperties(properties);
    }

    @Override
    public ComponentBuilder setProperty(String property, String value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, Map<String, Object> value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(CustomUIComponentProperty property) {
        return this.componentBuilder.setProperty(property);
    }

    @Override
    public ComponentBuilder setProperty(String property, int value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, boolean value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, double value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, float value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, long value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, short value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, byte value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, char value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, boolean[] value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder setProperty(String property, char[] value) {
        return this.componentBuilder.setProperty(property, value);
    }

    @Override
    public ComponentBuilder addComment(String comment) {
        return this.componentBuilder.addComment(comment);
    }
}
