package com.machina.minterfacebuilder.util.customui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.FnCall;
import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.SerializationUtil;
import com.machina.minterfacebuilder.util.StyleStringParser;
import com.machina.shared.util.ObjectUtil;

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
    public static final String INDENT = "  ";

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
    private Map<String, Object> styles = new HashMap<>();

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

        // Get the id of the component
        String propId = this.getPropertyIgnoreCase("Id");
        String id = propId != null ? propId.toString() : this.id;

        // If the component is blank and the id is not null
        if (this.component.isBlank() && id != null) {
            // Add the id
            builder.append("#" + id);
        } else {
            // Add the component
            builder.append(this.component);

            // If the id is not null, add it
            if (id != null) {
                builder.append(" #" + id);
            }
        }

        builder.append(" {\n");
        //#endregion

        // Copy the properties
        Map<String, Object> propertiesCopy = new HashMap<>(this.properties);

        // Remove the id from the properties
        propertiesCopy.remove(getPropertyNameIgnoreCase("Id"));

        //#region Component styles
        Object styleMap = parseStyles();

        // If the style map is not empty, add it to the properties
        if (styleMap != null) {
            // If the style map is a map, format it
            if (styleMap instanceof Map<?, ?>) {
                styleMap = formatNestedMap((Map<?, ?>) styleMap, componentIndent + 1, NestingStyle.PARENTHESIS);
            } else
            // If it's a string
            if (styleMap instanceof String) {
                // If it's empty, ignore it
                if (((String) styleMap).isBlank()) {
                    styleMap = null;
                }
            }

            // If the style map is not null, add it to the properties
            if (styleMap != null) {
                propertiesCopy.put("Style", LiteralValue.of(styleMap));
            }
        }

        //#endregion

        //#region Component properties
        List<String> propertiesOutput = new ArrayList<>();

        // Add the properties to the list
        for (Map.Entry<String, Object> entry : propertiesCopy.entrySet()) {
            Object valueObj = entry.getValue();

            // Group does not support Text property - skip it (it should be converted to Label child)
            if (entry.getKey().equals("Text") && this.component.equalsIgnoreCase("Group")) {
                continue;
            }

            String value;

            // If the value is a map, format it
            if (valueObj instanceof Map<?, ?>) {
                // Format the nested map
                value = formatNestedMap(
                    (Map<?, ?>) valueObj, componentIndent + 1, NestingStyle.PARENTHESIS
                );
            } else {
                value = formatPropertyValue(valueObj, componentIndent + 1);
            }

            // If the value is blank, skip it
            if (ObjectUtil.isBlank(value)) {
                continue;
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
     * Append children to the component.
     * @param children The children to append.
     * @return The builder instance.
     */
    public ComponentBuilder appendChild(ComponentBuilder... children) {
        for (ComponentBuilder child : children) {
            this.appendChild(child);
        }

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
    public ComponentBuilder setStyle(String style, Object value) {
        this.styles.put(style, value);
        return this;
    }

    /**
     * Set a style for the component.
     * @param styles The styles to set.
     * @return The builder instance.
     */
    public ComponentBuilder setStyle(Map<String, Object> styles) {
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
     * Parse the styles of the component.
     * @return The style map.
     */
    private Object parseStyles() {
        // Get existing Style Map from properties (if any) - this may contain nested Maps
        Map<String, Object> styleMap = new HashMap<>();
        Object existingStyle = this.properties.get("Style");

        // If an style property exists
        if (existingStyle != null) {
            // If the existing style is a map, add it to the style map
            if (existingStyle instanceof Map<?, ?>) {
                styleMap.putAll((Map<? extends String, ? extends Object>) existingStyle);
            } else {
                // If the existing style is a string, parse it
                if (existingStyle instanceof String) {
                    styleMap.putAll(StyleStringParser.parseKeyValuePairs((String) existingStyle));
                }

                // If it's a LiteralValue
                if (existingStyle instanceof LiteralValue) {
                    // If the literal value is a FnCall, return the value of the FnCall
                    if (existingStyle instanceof FnCall) {
                        return ((FnCall) existingStyle).getValue(this.getComponentBlockIndentLevel() + 1);
                    }

                    return ((LiteralValue) existingStyle).getValue();
                } else {
                    throw new IllegalArgumentException(
                        "The existing style is not a map or a string: " + existingStyle.getClass().getName()
                    );
                }
            }
        }

        // If there are styles to add
        if (!this.styles.isEmpty()) {
            // Add the styles to the map (only if not already present from Style property and not a Map)
            for (Map.Entry<String, Object> entry : this.styles.entrySet()) {
                var value = entry.getValue();

                // If the value is a map
                if (value instanceof Map<?, ?>) {
                    // If the style map does not contain the key, add it
                    if (!styleMap.containsKey(entry.getKey())) {
                        styleMap.put(entry.getKey(), value);
                        continue;
                    }

                    // Get the existing map
                    @SuppressWarnings("unchecked")
                    Map<String, Object> existing = (Map<String, Object>) styleMap.get(entry.getKey());

                    // If the existing map is not null and is a map
                    if (existing != null && existing instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> valueMap = (Map<String, Object>) value;
                        existing.putAll(valueMap);
                    } else {
                        // What do we do?
                        throw new IllegalArgumentException(
                            "A style map already exists for "
                            + entry.getKey()
                            + " but the value is not a map: "
                            + value.getClass().getName()
                        );
                    }

                    continue;
                }

                // If the key already exists
                if (styleMap.containsKey(entry.getKey())) {
                    // Skip it
                    continue;
                }

                // Put the value into the map
                styleMap.put(entry.getKey(), value);
            }
        }

        return styleMap;
    }

    /**
     * Convert a value to a string.
     * @param value The value to convert.
     * @return The string representation of the value.
     */
    public static String stringifyValue(Object value) {
        return stringifyValue(value, 0);
    }

    /**
     * Convert a value to a string.
     * @param value The value to convert.
     * @return The string representation of the value.
     */
    public static String stringifyValue(Object value, int indentLevel) {
        // If it's a function call, convert it to a string
        if (value instanceof FnCall) {
            return ((FnCall) value).getValue(0);
        }

        // If it's an InterfaceLiteral, convert it to a string
        if (value instanceof LiteralValue) {
            return ((LiteralValue) value).getValue();
        }

        // If it's a primitive serializable type, convert it to a string
        if (SerializationUtil.isSerializable(value)) {
            // If is a string
            if (value instanceof String) {
                String trimmed = ((String) value).trim();

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

                return '"' + trimmed + '"';
            }

            // It is already directly serializable, so return it as a string
            return value.toString();
        }

        throw new IllegalArgumentException("Value is not a serializable type: " + value.getClass().getName());
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
     * Get a property for the component.
     * @param property The property to get.
     * @return The value of the property.
     */
    public <T> T getPropertyIgnoreCase(String property) {
        return (T) this.properties.get(getPropertyNameIgnoreCase(property));
    }

    /**
     * Get the name of a property for the component.
     * @param property The property to get.
     * @return The name of the property.
     */
    public String getPropertyNameIgnoreCase(String property) {
        for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(property)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Set a property for the component.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return The builder instance.
     */
    public ComponentBuilder setProperty(String property, Object value) {
        // If the property is "id", set the id of the component
        if (property.toLowerCase() == "id") {
            this.setId(value.toString());
            return this;
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
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            this.setProperty(entry.getKey(), entry.getValue());
        }

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
        // If the value is a map
        if (value instanceof Map<?, ?>) {
            // If a map already exists for the property, merge it with the new map
            if (this.properties.containsKey(property)) {
                Map<?, ?> current = (Map<?, ?>) this.properties.get(property);

                // Create a mutable copy if the existing map is immutable
                Map<String, Object> merged = new java.util.HashMap<>();
                merged.putAll((Map<String, Object>) current);
                merged.putAll(value);

                this.properties.put(property, merged);
                return this;
            }
        }

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
     * Format a property value, adding quotes if needed.
     * Strings that are not i18n paths (starting with %), variables (starting with @),
     * numbers, booleans, or hex colors need quotes.
     * @param value The property value string.
     * @param propertyName The property name (to determine if it needs special handling).
     * @return The formatted value with quotes if needed.
     */
    private static String formatPropertyValue(Object value, int indentLevel) {
        // If null or empty
        if (value == null) {
            return "";
        }

        return stringifyValue(value, indentLevel);
    }

    /**
     * Format a nested Map recursively.
     * @param map The map to format.
     * @param indentLevel The indentation level.
     * @param nestingStyle The nesting style to use.
     * @return The formatted map string.
     */
    public static String formatNestedMap(Map<?, ?> map, int indentLevel, NestingStyle nestingStyle) {
        StringBuilder builder = new StringBuilder();

        // Get the nesting open and close characters
        String nestingOpen = nestingStyle == NestingStyle.PARENTHESIS ? "(" : "{";
        String nestingClose = nestingStyle == NestingStyle.PARENTHESIS ? ")" : "}";

        List<String> valueContent = new ArrayList<>();
        String indentStr = INDENT.repeat(indentLevel + 1);

        for (Map.Entry<?, ?> subEntry : map.entrySet()) {
            String subValue;
            Object subValueObj = subEntry.getValue();

            if (subValueObj instanceof Map<?, ?>) {
                // Recursively format nested maps
                // Children will always be formatted with parenthesis
                subValue = formatNestedMap(
                    (Map<?, ?>) subValueObj,
                    indentLevel + 1,
                    NestingStyle.PARENTHESIS
                );
            } else {
                // Other Map properties use normal formatting
                // This will be formatted with quotes if needed
                subValue = formatPropertyValue(subValueObj, indentLevel + 1);
            }

            valueContent.add(indentStr + subEntry.getKey() + ": " + subValue);
        }

        // Join the values with a comma and a new line
        String valuesContent = String.join(",\n", valueContent);

        // If there are values, add the nesting open and close characters
        if (!valuesContent.isBlank()) {
            builder.append(nestingOpen + "\n");
            builder.append(valuesContent);
            builder.append("\n" + INDENT.repeat(indentLevel) + nestingClose);
        }

        return builder.toString();
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

    public enum NestingStyle {
        PARENTHESIS,
        CURLY_BRACES
    }
}
