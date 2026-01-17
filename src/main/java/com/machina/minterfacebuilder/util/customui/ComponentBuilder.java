package com.machina.minterfacebuilder.util.customui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentBuilder {
    /**
     * Root directory path used for UI file imports.
     * Default value: "../"
     * This is prepended to all UI file import paths.
     */
    private static String ROOT_DIR = "../";

    /**
     * The indent character.
     */
    private static final String INDENT = "  ";

    /**
     * All properties that should be parsed and treated as a map.
     */
    private static final List<String> MAP_PROPERTIES = List.of("anchor", "style");

    /**
     * The component type.
     */
    private String component;

    /**
     * The id of the component.
     */
    private String id;

    /**
     * The properties of the component.
     */
    private Map<String, Object> properties = new HashMap<>();

    /**
     * The styles of the component.
     */
    private Map<String, String> styles = new HashMap<>();

    /**
     * The children of the component.
     */
    private List<Object> children = new ArrayList<>();

    /**
     * The comments of the component.
     */
    private List<String> comments = new ArrayList<>();

    /**
     * The variables of the component.
     */
    private Map<String, String> variables = new HashMap<>();

    /**
     * The parent of the component.
     */
    private ComponentBuilder parent;

    /**
     * The settings of the component builder.
     */
    private ComponentBuilderSettings settings = new ComponentBuilderSettings();

    /**
     * Create a new component builder.
     * @param component The component type.
     * @return The component builder.
     */
    public static ComponentBuilder create(String component) {
        return new ComponentBuilder(null, component);
    }

    /**
     * Create a new component builder.
     * @param parent The parent of the component.
     * @param component The component type.
     * @return The component builder.
     */
    public static ComponentBuilder create(String component, ComponentBuilder parent) {
        return new ComponentBuilder(parent, component);
    }

    /**
     * Create a new component builder.
     * @param parent The parent of the component.
     * @param component The component type.
     * @return The component builder.
     */
    public ComponentBuilder(String component) {
        this.component = component;
    }

    /**
     * Create a new component builder.
     * @param component The component type.
     */
    public ComponentBuilder(ComponentBuilder parent, String component) {
        this.parent = parent;
        this.component = component;
    }

    /**
     * Build the component with the given settings.
     * @param settings The settings to use for the component.
     * @return The component as a string.
     */
    public String build(ComponentBuilderSettings settings) {
        this.settings = settings;
        return this.build();
    }

    /**
     * Build the component.
     * @return The component as a string.
     */
    public String build() {
        StringBuilder builder = new StringBuilder();

        int componentIndent = getComponentBlockIndentLevel();

        //#region Variables
        for (Map.Entry<String, String> entry : this.variables.entrySet()) {
            builder.append(indent(componentIndent) + "@" + entry.getKey() + " = " + entry.getValue() + ";\n");
        }

        if (!this.variables.isEmpty()) {
            builder.append("\n");
        }
        //#endregion

        //#region Component comments
        if (!this.settings.minimal) {
            for (String comment : this.comments) {
                builder.append(indent(componentIndent) + "// " + comment + "\n");
            }
        }
        //#endregion

        //#region Component header
        // Root component has no indent
        builder.append(indent(componentIndent));
        builder.append(this.component);

        // Add the id if it exists
        if (this.id != null) {
            builder.append(" #" + this.id);
        }

        builder.append(" {\n");
        //#endregion

        // Copy the properties
        Map<String, Object> properties = new HashMap<>(this.properties);

        //#region Component styles
        // Get existing Style Map from properties (if any) - this may contain nested Maps
        Map<String, Object> styleMap = null;
        Object existingStyle = this.properties.get("Style");
        if (existingStyle instanceof Map<?, ?>) {
            // Preserve existing Style Map which may contain nested Maps (like Sounds, EntrySounds)
            // Deep copy to preserve Maps as Maps (not as references that might get converted)
            styleMap = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) existingStyle).entrySet()) {
                // Preserve Maps as-is (don't convert to String)
                // HashMap constructor already preserves Map references, so just copy directly
                styleMap.put(entry.getKey().toString(), entry.getValue());
            }
        } else {
            // Build new Style Map from scratch
            styleMap = new HashMap<>();
        }

        // Get the "Styles" property
        String stylesFromProperties = (String) this.properties.get("Styles");

        // Add the styles from the properties to the map (only if not already present and not a Map)
        if (stylesFromProperties != null) {
            String[] stylesFromPropertiesArray = stylesFromProperties.split(",");
            for (String style : stylesFromPropertiesArray) {
                style = style.trim();
                if (style.isEmpty()) continue;
                int colonIndex = style.indexOf(':');
                if (colonIndex != -1) {
                    String key = style.substring(0, colonIndex).trim();
                    // Don't overwrite Maps with Strings
                    if (!styleMap.containsKey(key) || !(styleMap.get(key) instanceof Map<?, ?>)) {
                        String value = style.substring(colonIndex + 1).trim();
                        // Remove quotes if present
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        styleMap.put(key, value);
                    }
                }
            }
        }

        // Add the styles to the map (only if not already present from Style property and not a Map)
        for (Map.Entry<String, String> entry : this.styles.entrySet()) {
            if (!styleMap.containsKey(entry.getKey()) || !(styleMap.get(entry.getKey()) instanceof Map<?, ?>)) {
                String value = entry.getValue();
                // Remove quotes if present
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                styleMap.put(entry.getKey(), value);
            }
        }

        if (!styleMap.isEmpty()) {
            // Add the styles to the component as a Map so it's formatted correctly without quotes
            properties.put("Style", styleMap);
        }
        //#endregion

        //#region Component properties
        List<String> propertiesOutput = new ArrayList<>();

        // Add the properties to the list
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            // Group does not support Text property - skip it (it should be converted to Label child)
            if (entry.getKey().equals("Text") && this.component.equalsIgnoreCase("Group")) {
                continue;
            }
            String value;

            if (entry.getValue() instanceof Map<?, ?>) {
                value = formatNestedMap((Map<?, ?>) entry.getValue(), componentIndent + 1, entry.getKey().equals("Style"));
            } else {
                value = formatPropertyValue(entry.getValue().toString(), entry.getKey());
            }

            propertiesOutput.add(
                entry.getKey()
                + ": "
                + value
            );
        }

        String propertiesContent = String.join(";\n" + indent(componentIndent + 1), propertiesOutput);
        if (!propertiesContent.isBlank()) {
            builder.append(indent(componentIndent + 1) + propertiesContent + ";\n");
        }
        //#endregion

        // If there are children, add a new line
        if (!this.children.isEmpty() && (!propertiesContent.isBlank() || !this.styles.isEmpty()) && !this.settings.minimal) {
            builder.append("\n");
        }

        int childIndex = 0;

        //#region Component children
        for (Object child : this.children) {
            childIndex++;

            String value;

            // If the child is a string, add it to the list
            if (child instanceof String) {
                value = indent(componentIndent + 1) + ((String) child).trim();
            } else
            // If the child is a component builder, add it to the list
            if (child instanceof ComponentBuilder) {
                value = ((ComponentBuilder) child).build(this.settings);
            } else {
                throw new IllegalArgumentException("Child is not a string or component builder: " + child.getClass().getName());
            }

            builder.append(value + "\n");

            // Jump two lines between children if not the last child
            if (childIndex < this.children.size()) {
                builder.append("\n");
            }
        }

        builder.append(indent(componentIndent) + "}");
        //#endregion

        return builder.toString();
    }

    /**
     * Append a child to the component.
     * @param child The child to append.
     * @return The builder instance.
     */
    public ComponentBuilder appendChild(String child) {
        this.children.add(child);
        return this;
    }

    /**
     * Append a child to the component.
     * @param child The child to append.
     * @return The builder instance.
     */
    public ComponentBuilder appendChild(ComponentBuilder child) {
        this.children.add(child);
        child.setParent(this);

        return this;
    }

    /**
     * Set the settings of the component builder.
     * @param settings The settings to set.
     * @return The builder instance.
     */
    public ComponentBuilder setSettings(ComponentBuilderSettings settings) {
        this.settings = settings;
        return this;
    }

    /**
     * Set a variable for the component.
     * @param variable The variable to set.
     * @param value The value to set the variable to.
     * @return The builder instance.
     */
    public ComponentBuilder setVariable(String variable, String value) {
        String cleanVar = variable.replaceFirst("^@", "");
        // #region agent log - track _MI_ additions
        if (cleanVar.startsWith("_MI_")) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String caller = stack.length > 2 ? stack[2].toString() : "unknown";
            System.err.println("[DEBUG-B] setVariable called with _MI_ | var=" + cleanVar + " | value=" + value + " | caller=" + caller);
        }
        // #endregion
        this.variables.put(cleanVar, value);
        return this;
    }

    /**
     * Set a variable for the component.
     * @param variables The variables to set.
     * @param value The value to set the variable to.
     * @return The builder instance.
     */
    public ComponentBuilder setVariable(Map<String, String> variables) {
        // #region agent log - track _MI_ additions in bulk
        for (String key : variables.keySet()) {
            if (key.startsWith("_MI_") || key.startsWith("@_MI_")) {
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                String caller = stack.length > 2 ? stack[2].toString() : "unknown";
                System.err.println("[DEBUG-B] setVariable(Map) called with _MI_ | var=" + key + " | caller=" + caller);
            }
        }
        // #endregion
        this.variables.putAll(variables);
        return this;
    }
    
    // #region agent log - temporary getters
    public Map<String, String> getBuilderVariables() {
        return this.variables;
    }
    
    public ComponentBuilderSettings getSettings() {
        return this.settings;
    }
    // #endregion

    /**
     * Set the parent of the component.
     * @param parent The parent to set.
     * @return The builder instance.
     */
    public ComponentBuilder setParent(ComponentBuilder parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Set a style for the component.
     * @param style The style to set.
     * @param value The value to set the style to.
     * @return The builder instance.
     */
    public ComponentBuilder setStyle(String style, String value) {
        this.styles.put(style, value);
        return this;
    }

    /**
     * Set a style for the component.
     * @param style The style to set.
     * @param value The value to set the style to.
     * @return The builder instance.
     */
    public ComponentBuilder setStyle(Map<String, String> styles) {
        this.styles.putAll(styles);
        return this;
    }

    /**
     * Set the id for the component.
     * IDs are automatically converted to PascalCase (e.g., "my-button" -> "MyButton", "my_id" -> "MyId").
     * @param id The id to set.
     * @return The builder instance.
     */
    public ComponentBuilder setId(String id) {
        String pascalId = toPascalCase(id);

        // Prohibit using MIBRoot as ID (reserved for root container)
        // Check both original and PascalCase versions to catch all variations
        if (pascalId != null && pascalId.equalsIgnoreCase("MIBRoot")) {
            throw new IllegalArgumentException(
                "ID 'MIBRoot' (or any case variation like '" + id + "' -> '" + pascalId + "') is reserved for the root container " +
                "and cannot be used for UI components. Please use a different ID."
            );
        }

        this.id = pascalId;
        return this;
    }

    /**
     * Convert a string to PascalCase.
     * Handles snake_case, kebab-case, and camelCase conversions.
     * @param input The input string.
     * @return The PascalCase version of the string.
     */
    private static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Split by underscore, hyphen, or camelCase boundary
        String[] parts = input.split("[_\\-]|(?<=[a-z])(?=[A-Z])");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

    /**
     * Get a property for the component.
     * @param property The property to get.
     * @return The value of the property.
     */
    public <T> T getProperty(String property) {
        var value = this.properties.get(property);

        return (T) value;
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, Object value) {
        if (MAP_PROPERTIES.contains(property)) {
            // If it's already a map
            if (value instanceof Map<?, ?>) {
                this.properties.put(property, (Map<?, ?>) value);
            } else
            // If it's a string
            if (value instanceof String) {
                // Parse the string as a map
                Map<String, Object> map = new HashMap<>();
                String[] keyValuePairs = ((String) value).split(",");

                for (String keyValuePair : keyValuePairs) {
                    String[] keyValue = keyValuePair.split(":");
                    map.put(keyValue[0].trim(), keyValue[1].trim());
                }

                this.properties.put(property, map);
            }
        }

        this.properties.put(property, value);
        return this;
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, String value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, Map<String, Object> value) {
        return this.setProperty(property, (Object) new HashMap<>(value));
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(CustomUIComponentProperty property) {
        return this.setProperty(property.getKey(), property.getValue());
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, int value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, boolean value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, double value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, float value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, long value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, short value) {
        return this.setProperty(property, (Object) value);
    }
    
    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, byte value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, char value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, boolean[] value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, char[] value) {
        return this.setProperty(property, (Object) value);
    }

    /**
     * Get the current root directory path.
     * @return The root directory path.
     */
    public static String getRootDir() {
        return ROOT_DIR;
    }

    /**
     * Set the root directory path for UI file imports.
     * @param rootDir The root directory path (e.g., "../" or "../../").
     */
    public static void setRootDir(String rootDir) {
        ROOT_DIR = rootDir;
    }

    /**
     * Build a UI file import path using ROOT_DIR.
     * @param fileName The UI file name (e.g., "Common.ui").
     * @return The full import path (e.g., "../Common.ui").
     */
    public static String buildImportPath(String fileName) {
        return ROOT_DIR + fileName;
    }

    /**
     * Add a comment to the component.
     * @param comment The comment to add.
     * @return The builder instance.
     */
    public ComponentBuilder addComment(String comment) {
        this.comments.add(comment);
        return this;
    }

    /**
     * Get the indent level for the component's opening/closing braces.
     * Root component has 0 indent. First level adds 2 tabs, subsequent levels add 1 tab.
     * @return The indent level (number of tabs).
     */
    private int getComponentBlockIndentLevel() {
        // Root component has 0 indent
        if (this.parent == null) {
            return 0;
        }

        // Start at level 0
        int level = 0;

        // Start from the current component and count the levels up to the root
        ComponentBuilder current = this;

        // Count the levels up to the root
        while (current.parent != null) {
            level++;
            current = current.parent;
        }

        return level;
    }

    /**
     * Generates a string with the specified number of indent characters.
     * @param level The number of indent characters to generate.
     * @return A string consisting of 'level' indent characters.
     */
    private String indent(int level) {
        // If the settings are set to minimal, return an empty string
        if (this.settings != null && this.settings.minimal) {
            return "";
        }

        return INDENT.repeat(level);
    }

    /**
     * Format a Style property value (no quotes inside Style objects).
     * Style properties should never have quotes - values like "Center" should be Center.
     * @param value The style property value string.
     * @return The formatted value without quotes.
     */
    private static String formatStylePropertyValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String trimmed = value.trim();

        // Remove quotes if present (they shouldn't be there in Style)
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || 
            (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        // Numbers, booleans, colors, etc. - return as-is
        // Everything else - return as-is without quotes (Style values don't use quotes)
        return trimmed;
    }

    /**
     * Format a property value, adding quotes if needed.
     * Strings that are not i18n paths (starting with %), variables (starting with @),
     * numbers, booleans, or hex colors need quotes.
     * @param value The property value string.
     * @param propertyName The property name (to determine if it needs special handling).
     * @return The formatted value with quotes if needed.
     */
    private static String formatPropertyValue(String value, String propertyName) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String trimmed = value.trim();

        // i18n paths (start with %) - no quotes
        if (trimmed.startsWith("%")) {
            // Convert i18n paths to camelCase if they contain underscores or hyphens
            return toCamelCaseI18nPath(trimmed);
        }

        // Variables (start with @) - no quotes
        if (trimmed.startsWith("@")) {
            return trimmed;
        }

        // Numbers (integer or decimal) - no quotes
        if (trimmed.matches("-?\\d+(\\.\\d+)?")) {
            return trimmed;
        }

        // Booleans - no quotes
        if (trimmed.equals("true") || trimmed.equals("false")) {
            return trimmed;
        }

        // Hex colors (start with #) - no quotes, but expand 3-digit to 6-digit
        if (trimmed.startsWith("#")) {
            return expandHexColor(trimmed);
        }

        // Everything else needs quotes
        return "\"" + trimmed + "\"";
    }

    /**
     * Convert an i18n path to camelCase.
     * i18n paths don't support underscores, hyphens, or dots - they must be camelCase.
     * Examples: "%ui.button.submit" -> "%uiButtonSubmit", "%ui_button_submit" -> "%uiButtonSubmit"
     * @param path The i18n path.
     * @return The camelCase version of the path.
     */
    private static String toCamelCaseI18nPath(String path) {
        if (!path.startsWith("%")) {
            return path;
        }

        String rest = path.substring(1); // Remove the %
        
        // Split by underscore, hyphen, or dot
        String[] parts = rest.split("[_\\-\\.]");
        StringBuilder result = new StringBuilder("%");
        
        boolean first = true;
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (first) {
                    // First part starts with lowercase
                    result.append(part.toLowerCase());
                    first = false;
                } else {
                    // Subsequent parts start with uppercase (PascalCase within camelCase)
                    result.append(Character.toUpperCase(part.charAt(0)));
                    if (part.length() > 1) {
                        result.append(part.substring(1).toLowerCase());
                    }
                }
            }
        }

        return result.toString();
    }

    /**
     * Expand a 3-digit hex color to 6-digit format.
     * @param hex The hex color (e.g., "#ccc" -> "#cccccc", "#abc" -> "#aabbcc").
     * @return The expanded hex color.
     */
    private static String expandHexColor(String hex) {
        if (hex == null || !hex.startsWith("#")) {
            return hex;
        }

        // If already 6 or 8 digits, return as-is
        if (hex.length() == 7 || hex.length() == 9) {
            return hex;
        }

        // If 4 characters (#RGB), expand to 6 (#RRGGBB)
        if (hex.length() == 4) {
            return "#" + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2) + hex.charAt(3) + hex.charAt(3);
        }

        return hex;
    }

    /**
     * Format a nested Map recursively.
     * @param map The map to format.
     * @param indentLevel The indentation level.
     * @param isStyle Whether this is a Style property (affects formatting).
     * @return The formatted map string.
     */
    private static String formatNestedMap(Map<?, ?> map, int indentLevel, boolean isStyle) {
        StringBuilder builder = new StringBuilder();
        builder.append("(\n");
        
        List<String> valueContent = new ArrayList<>();
        String indentStr = INDENT.repeat(indentLevel + 1);
        
        for (Map.Entry<?, ?> subEntry : map.entrySet()) {
            String subValue;
            Object subValueObj = subEntry.getValue();
            
            if (subValueObj instanceof Map<?, ?>) {
                // Recursively format nested maps
                subValue = formatNestedMap((Map<?, ?>) subValueObj, indentLevel + 1, false);
            } else if (isStyle) {
                // Style properties should not have quotes
                subValue = formatStylePropertyValue(subValueObj.toString());
            } else {
                // Other Map properties use normal formatting
                subValue = formatPropertyValue(subValueObj.toString(), "");
            }
            
            valueContent.add(indentStr + subEntry.getKey() + ": " + subValue);
        }

        String indentStr2 = INDENT.repeat(indentLevel);
        builder.append(String.join(",\n", valueContent));
        builder.append("\n" + indentStr2 + ")");
        
        return builder.toString();
    }
}
