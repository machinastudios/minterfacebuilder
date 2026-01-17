package com.machina.minterfacebuilder.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.ComponentBuilderSettings;

/**
 * Represents a parsed Custom UI template with support for variables and file persistence.
 * Extends ComponentBuilder - the template itself IS the component builder.
 */
public class HTMLCustomUITemplate extends ComponentBuilder {
    /**
     * The variables defined in the template (e.g., @Valor = "Sim").
     */
    private final Map<String, InterfaceVariable> variables;

    /**
     * The HTML tags used in the template that need aliases (e.g., h1, h2, span, p, label).
     */
    private final Set<String> usedHtmlTags;

    /**
     * Aliases used in the template (e.g., "Common", "C") for generating alias declarations.
     */
    private Set<String> usedAliases;

    /**
     * Custom aliases map (e.g., "Common" -> "../Common.ui") for generating alias declarations.
     */
    private Map<String, String> customAliasesMap;

    /**
     * Default path for saving/loading template files (e.g., config/templates/).
     */
    @Nullable
    private Path defaultPath;

    /**
     * Cached build result to avoid recompiling on multiple calls.
     */
    @Nullable
    private String cachedBuildResult;

    /**
     * Settings used for the cached build result (to ensure cache is invalidated if settings change).
     */
    @Nullable
    private ComponentBuilderSettings cachedBuildSettings;

    /**
     * Flag to indicate if the template has been modified since the last build.
     * When true, the cache is invalidated.
     */
    private boolean dirty = false;

    /**
     * Create a new HTMLCustomUITemplate from a ComponentBuilder.
     * The ComponentBuilder becomes this instance (we copy its properties).
     * 
     * @param componentBuilder The ComponentBuilder to use as the base.
     * @param variables The variables map (InterfaceVariable instances).
     */
    public HTMLCustomUITemplate(ComponentBuilder componentBuilder, Map<String, InterfaceVariable> variables) {
        this(componentBuilder, variables, new HashSet<>());
    }

    /**
     * Create a new HTMLCustomUITemplate from a ComponentBuilder.
     * The ComponentBuilder becomes this instance (we copy its properties).
     * 
     * @param componentBuilder The ComponentBuilder to use as the base.
     * @param variables The variables map (InterfaceVariable instances).
     * @param usedHtmlTags The HTML tags used in the template that need aliases.
     */
    public HTMLCustomUITemplate(ComponentBuilder componentBuilder, Map<String, InterfaceVariable> variables, Set<String> usedHtmlTags) {
        super(getComponentType(componentBuilder));
        
        // Copy all properties from the component builder
        this.copyFrom(componentBuilder);
        
        this.variables = new HashMap<>(variables);
        this.usedHtmlTags = new HashSet<>(usedHtmlTags != null ? usedHtmlTags : new HashSet<>());
        this.usedAliases = new HashSet<>();
        this.customAliasesMap = new HashMap<>();
        this.defaultPath = null;
    }

    /**
     * Set used aliases and their paths for output generation.
     * @param usedAliases Set of alias names used in the template (e.g., "Common", "C").
     * @param customAliasesMap Map of alias names to their paths (e.g., "Common" -> "../Common.ui").
     */
    public void setUsedAliases(Set<String> usedAliases, Map<String, String> customAliasesMap) {
        this.usedAliases = usedAliases != null ? new HashSet<>(usedAliases) : new HashSet<>();
        this.customAliasesMap = customAliasesMap != null ? new HashMap<>(customAliasesMap) : new HashMap<>();
    }

    /**
     * Get the component type from a ComponentBuilder using reflection.
     * @param builder The ComponentBuilder.
     * @return The component type string.
     */
    private static String getComponentType(ComponentBuilder builder) {
        try {
            java.lang.reflect.Field componentField = ComponentBuilder.class.getDeclaredField("component");
            componentField.setAccessible(true);
            return (String) componentField.get(builder);
        } catch (Exception e) {
            // Fallback to "Group" if we can't get the component type
            return "Group";
        }
    }

    /**
     * Copy properties from another ComponentBuilder to this instance using reflection.
     * @param other The ComponentBuilder to copy from.
     */
    @SuppressWarnings("unchecked")
    private void copyFrom(ComponentBuilder other) {
        try {
            // Use reflection to copy all fields from the other ComponentBuilder
            java.lang.reflect.Field idField = ComponentBuilder.class.getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(other);
            if (id != null) {
                idField.set(this, id);
            }

            java.lang.reflect.Field propertiesField = ComponentBuilder.class.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            Map<String, Object> props = (Map<String, Object>) propertiesField.get(other);
            if (props != null && !props.isEmpty()) {
                Map<String, Object> thisProps = (Map<String, Object>) propertiesField.get(this);
                thisProps.putAll(props);
            }

            java.lang.reflect.Field stylesField = ComponentBuilder.class.getDeclaredField("styles");
            stylesField.setAccessible(true);
            Map<String, String> styles = (Map<String, String>) stylesField.get(other);
            if (styles != null && !styles.isEmpty()) {
                Map<String, String> thisStyles = (Map<String, String>) stylesField.get(this);
                thisStyles.putAll(styles);
            }

            java.lang.reflect.Field childrenField = ComponentBuilder.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            List<Object> children = (List<Object>) childrenField.get(other);
            if (children != null && !children.isEmpty()) {
                List<Object> thisChildren = (List<Object>) childrenField.get(this);
                thisChildren.addAll(children);
            }

            java.lang.reflect.Field variablesField = ComponentBuilder.class.getDeclaredField("variables");
            variablesField.setAccessible(true);
            Map<String, String> variables = (Map<String, String>) variablesField.get(other);
            // #region agent log - copyFrom variables
            if (variables != null) {
                java.util.List<String> miVars = variables.keySet().stream().filter(k -> k.startsWith("_MI_")).collect(java.util.stream.Collectors.toList());
                System.err.println("[DEBUG-D] copyFrom copying variables | total=" + variables.size() + " | miVars=" + miVars);
            }
            // #endregion
            if (variables != null && !variables.isEmpty()) {
                Map<String, String> thisVariables = (Map<String, String>) variablesField.get(this);
                thisVariables.putAll(variables);
            }

            java.lang.reflect.Field commentsField = ComponentBuilder.class.getDeclaredField("comments");
            commentsField.setAccessible(true);
            List<String> comments = (List<String>) commentsField.get(other);
            if (comments != null && !comments.isEmpty()) {
                List<String> thisComments = (List<String>) commentsField.get(this);
                thisComments.addAll(comments);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy ComponentBuilder properties", e);
        }
    }

    /**
     * Set the default path for saving/loading template files.
     * This path will be used by saveToFile() and loadFromFile() if no path is provided.
     * 
     * @param defaultPath The default path (e.g., Path.of("config/templates/")).
     * @return This instance for chaining.
     */
    @Nonnull
    public HTMLCustomUITemplate setDefaultPath(@Nullable Path defaultPath) {
        this.defaultPath = defaultPath;
        return this;
    }

    /**
     * Get the default path for saving/loading template files.
     * 
     * @return The default path, or null if not set.
     */
    @Nullable
    public Path getDefaultPath() {
        return this.defaultPath;
    }

    /**
     * Save this template to a file.
     * If path is null, uses the default path set via setDefaultPath().
     * Creates parent directories if they don't exist.
     * 
     * @param filename The filename (e.g., "mypage.html").
     * @param path The directory path (can be null to use defaultPath).
     * @throws IOException If the file cannot be written.
     */
    public void saveToFile(@Nonnull String filename, @Nullable Path path) throws IOException {
        Path targetPath = path != null ? path : this.defaultPath;
        if (targetPath == null) {
            throw new IllegalStateException("No path provided and defaultPath is not set. Call setDefaultPath() or provide a path.");
        }

        Path filePath = targetPath.resolve(filename);
        
        // Create parent directories if they don't exist
        Files.createDirectories(filePath.getParent());

        // Get the HTML source (we need to reconstruct it from the template)
        // For now, we'll save the compiled UI output
        String output = this.build(new ComponentBuilderSettings(false));
        
        // Save with .ui extension instead of .html
        String uiFilename = filename.endsWith(".html") ? filename.substring(0, filename.length() - 5) + ".ui" : filename + ".ui";
        Path uiFilePath = targetPath.resolve(uiFilename);
        
        Files.writeString(uiFilePath, output);
    }

    /**
     * Save this template to the default path.
     * 
     * @param filename The filename (e.g., "mypage.html").
     * @throws IOException If the file cannot be written.
     * @throws IllegalStateException If defaultPath is not set.
     */
    public void saveToFile(@Nonnull String filename) throws IOException {
        this.saveToFile(filename, null);
    }

    /**
     * Load template HTML from a file (input/output pattern).
     * <p>
     * This method implements a copy-once pattern:
     * </p>
     * <ol>
     * <li>First tries to load from the output path (user-customizable). If the file exists, uses it.</li>
     * <li>If output file doesn't exist, tries to load from the input path (default asset).</li>
     * <li>If input file is found, copies it to output path so user can customize it.</li>
     * <li>Once output file exists, it will always be used (input is never copied again).</li>
     * </ol>
     * 
     * @param filename The filename (e.g., "mypage.html").
     * @param outputPath The output directory path (user-customizable, can be null).
     * @param inputPath The input directory path (default asset inside mod, can be null).
     * @return The HTML content as a string, or null if not found in either location.
     * @throws IOException If there's an error reading or copying the file.
     */
    @Nullable
    public static String loadFromFile(@Nonnull String filename, @Nullable Path outputPath, @Nullable Path inputPath) throws IOException {
        // First, try to load from output path (user-customizable)
        // If this file exists, it takes precedence and input is never used
        if (outputPath != null) {
            Path outputFilePath = outputPath.resolve(filename);
            if (Files.exists(outputFilePath) && Files.isRegularFile(outputFilePath)) {
                return Files.readString(outputFilePath);
            }
        }

        // If output file doesn't exist, try to load from input path (default asset inside mod)
        if (inputPath != null) {
            // inputPath can be either a directory or a file
            Path inputFilePath = null;
            if (Files.isDirectory(inputPath)) {
                // inputPath is a directory - resolve filename
                inputFilePath = inputPath.resolve(filename);
            } else if (Files.isRegularFile(inputPath)) {
                // inputPath is a file - use it directly
                inputFilePath = inputPath;
            }
            
            if (inputFilePath != null && Files.exists(inputFilePath) && Files.isRegularFile(inputFilePath)) {
                // Copy to output path so user can customize it
                // This copy only happens once - after this, output file will always exist
                if (outputPath != null) {
                    Path targetOutputPath = outputPath.resolve(filename);
                    try {
                        Files.createDirectories(targetOutputPath.getParent());
                        Files.copy(inputFilePath, targetOutputPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new IOException("Failed to copy template file from input to output path. Input: " + inputFilePath + ", Output: " + targetOutputPath + ". Error: " + e.getMessage(), e);
                    }
                }
                return Files.readString(inputFilePath);
            }
        }

        // Not found in either location - throw exception with detailed message
        throw new java.io.FileNotFoundException("HTML template file not found: " + filename + 
            " (searched in output: " + outputPath + ", input: " + inputPath + ")");
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
    public HTMLCustomUITemplate setVariable(String name, String value) {
        // Remove @ prefix if present
        String varName = name.startsWith("@") ? name.substring(1) : name;
        this.variables.put(varName, InterfaceVariable.stringValue(value));
        this.dirty = true; // Mark as dirty when variables change
        return this;
    }

    /**
     * Set a variable value with type (template variable, not ComponentBuilder variable).
     * @param name The variable name (without @ prefix).
     * @param variable The InterfaceVariable instance.
     * @return This instance for chaining.
     */
    public HTMLCustomUITemplate setVariable(String name, InterfaceVariable variable) {
        // Remove @ prefix if present
        String varName = name.startsWith("@") ? name.substring(1) : name;
        this.variables.put(varName, variable);
        this.dirty = true; // Mark as dirty when variables change
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
     * Build a dynamic BuilderCodec based on the variables in this template.
     * <p>
     * This method creates a BuilderCodec that can handle all variables defined in the template.
     * The generated codec uses a generic EventData class that stores values in a Map.
     * </p>
     * <p>
     * <b>Example usage:</b>
     * </p>
     * <pre>
     * HTMLCustomUITemplate template = InterfaceBuilder.parse(html);
     * KeyedCodec&lt;DynamicEventData&gt; codec = template.buildEventDataCodec();
     * </pre>
     * 
     * @return A BuilderCodec that can handle all variables in this template.
     */
    public BuilderCodec<DynamicEventData> buildEventDataCodec() {
        BuilderCodec.Builder<DynamicEventData> builder = BuilderCodec.<DynamicEventData>builder(
            DynamicEventData.class,
            DynamicEventData::new
        );

        // Add a field for each variable in the template
        for (Map.Entry<String, InterfaceVariable> entry : this.variables.entrySet()) {
            String varName = entry.getKey();
            InterfaceVariable var = entry.getValue();
            String key = "@" + varName; // Use @ prefix for variable keys

            // Determine the Codec type based on the variable type
            // Add the field to the builder based on variable type
            // Note: addField uses KeyedCodec for individual fields, but the final result is a BuilderCodec
            switch (var.getType()) {
                case BOOLEAN:
                    builder.addField(
                        new KeyedCodec<>(key, Codec.BOOLEAN),
                        (DynamicEventData data, Boolean value) -> data.setValue(varName, value),
                        (DynamicEventData data) -> (Boolean) data.getValue(varName)
                    );
                    break;
                case COLOR:
                case LITERAL:
                case STRING:
                default:
                    // Colors, literals, and strings are all stored as strings
                    builder.addField(
                        new KeyedCodec<>(key, Codec.STRING),
                        (DynamicEventData data, String value) -> data.setValue(varName, value),
                        (DynamicEventData data) -> (String) data.getValue(varName)
                    );
                    break;
            }
        }

        return builder.build();
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
        // If template is dirty or cache is invalid, rebuild
        // Compare settings by value (not reference) to properly use cache
        boolean settingsChanged = settings == null ? 
            this.cachedBuildSettings != null :
            this.cachedBuildSettings == null || 
            this.cachedBuildSettings.minimal != settings.minimal;
        
        if (this.dirty || this.cachedBuildResult == null || settingsChanged) {
            // Build and cache the result
            String result = buildWithAliasesAndVariables(settings);
            this.cachedBuildResult = result;
            this.cachedBuildSettings = settings; // Store reference (can be null)
            this.dirty = false; // Clear dirty flag after rebuild
            return result;
        }
        
        // Return cached result
        return this.cachedBuildResult;
    }

    @Override
    public String build() {
        // Delegate to build(null) to use the same caching logic
        return build(null);
    }

    /**
     * Build the UI with aliases and variables at the top.
     * @param settings The settings to use (can be null).
     * @return The UI as a string with aliases and variables at the top.
     */
    /**
     * Enum for supported HTML tags that can be aliased to UI components.
     */
    private enum HTMLTag {
        H1("h1"), H2("h2"), H3("h3"), H4("h4"), H5("h5"), H6("h6"),
        SPAN("span"), P("p"), LABEL("label");
        
        private final String tagName;
        
        HTMLTag(String tagName) {
            this.tagName = tagName;
        }
        
        public String getTagName() {
            return tagName;
        }
        
        public static HTMLTag fromString(String tag) {
            for (HTMLTag htmlTag : values()) {
                if (htmlTag.tagName.equalsIgnoreCase(tag)) {
                    return htmlTag;
                }
            }
            return null;
        }
    }
    
    private String buildWithAliasesAndVariables(ComponentBuilderSettings settings) {
        StringBuilder output = new StringBuilder();

        // Build alias declarations for used aliases (e.g., $Common = "../Common.ui";)
        if (this.usedAliases != null && !this.usedAliases.isEmpty() && this.customAliasesMap != null) {
            for (String aliasName : this.usedAliases) {
                String aliasPath = this.customAliasesMap.get(aliasName);
                // If alias not found in customAliasesMap, use default path for Common/C
                if (aliasPath == null) {
                    if (aliasName.equalsIgnoreCase("Common")) {
                        aliasPath = "../Common.ui";
                    } else if (aliasName.equalsIgnoreCase("C")) {
                        aliasPath = "../Common.ui";
                    }
                }
                if (aliasPath != null) {
                    output.append("$").append(aliasName).append(" = \"").append(aliasPath).append("\";\n");
                }
            }
            if (!this.usedAliases.isEmpty()) {
                output.append("\n");
            }
        }

        // Build aliases for HTML tags that map to Label (h1-h6, span, p, label)
        // Format: @MIBH1 = Label { Style: (...) }
        Map<HTMLTag, Object> tagToComponentMap = Map.of(
            HTMLTag.H1, "Label",
            HTMLTag.H2, "Label",
            HTMLTag.H3, "Label",
            HTMLTag.H4, "Label",
            HTMLTag.H5, "Label",
            HTMLTag.H6, "Label",
            HTMLTag.SPAN, "Label",
            HTMLTag.P, "Label",
            HTMLTag.LABEL, "Label"
        );

        // Default font sizes for headings (h1 = largest, h6 = smallest)
        Map<HTMLTag, Object> headingFontSizes = Map.of(
            HTMLTag.H1, 28,
            HTMLTag.H2, 24,
            HTMLTag.H3, 20,
            HTMLTag.H4, 18,
            HTMLTag.H5, 16,
            HTMLTag.H6, 14
        );

        // Generate aliases for used HTML tags (use TreeSet to ensure consistent ordering and no duplicates)
        // Convert all tags to lowercase first to normalize them
        Set<HTMLTag> usedTagsSet = new java.util.LinkedHashSet<>();
        
        for (String tag : this.usedHtmlTags) {
            HTMLTag htmlTag = HTMLTag.fromString(tag);
            if (htmlTag != null) {
                usedTagsSet.add(htmlTag);
            }
        }
        
        for (HTMLTag htmlTag : usedTagsSet) {
            Object component = tagToComponentMap.get(htmlTag);
            if (component != null) {
                String tag = htmlTag.getTagName();
                // Convert tag to PascalCase (e.g., "h1" -> "H1", "p" -> "P")
                String pascalTag = tag.isEmpty() ? tag : Character.toUpperCase(tag.charAt(0)) + (tag.length() > 1 ? tag.substring(1) : "");
                String varName = "@MIB" + pascalTag;
                
                output.append(varName).append(" = ").append(component).append(" {\n");
                
                // Add default styles for headings (h1-h6)
                Object fontSize = headingFontSizes.get(htmlTag);
                if (fontSize != null) {
                    output.append("  Style: (\n");
                    output.append("    FontSize: ").append(fontSize).append(",\n");
                    output.append("    RenderBold: true\n");
                    output.append("  );\n");
                } else if (htmlTag == HTMLTag.P) {
                    // Add default margin-bottom for paragraphs
                    output.append("  Anchor: (\n");
                    output.append("    Bottom: 8\n");
                    output.append("  );\n");
                }
                
                output.append("};\n\n");
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

        // Build the component structure WITHOUT calling build() to avoid recursion
        // We directly build the component part (without variables section) to avoid infinite loop
        ComponentBuilderSettings oldSettings = this.getSettings();
        if (settings != null) {
            this.setSettings(settings);
        }
        
        String componentOutput = buildComponentWithoutVariables();
        
        // Restore old settings
        this.setSettings(oldSettings);
        
        output.append(componentOutput);
        
        return output.toString();
    }

    /**
     * Build the component structure without variables to avoid recursion.
     * This replicates ComponentBuilder.build() logic but skips the variables section
     * and doesn't call build() on children (which would cause recursion).
     */
    private String buildComponentWithoutVariables() {
        // Use reflection to access private methods from ComponentBuilder
        try {
            java.lang.reflect.Method getIndentMethod = ComponentBuilder.class.getDeclaredMethod("getComponentBlockIndentLevel");
            getIndentMethod.setAccessible(true);
            int componentIndent = (Integer) getIndentMethod.invoke(this);
            
            java.lang.reflect.Method indentMethod = ComponentBuilder.class.getDeclaredMethod("indent", int.class);
            indentMethod.setAccessible(true);
            
            java.lang.reflect.Field componentField = ComponentBuilder.class.getDeclaredField("component");
            componentField.setAccessible(true);
            String component = (String) componentField.get(this);
            
            java.lang.reflect.Field idField = ComponentBuilder.class.getDeclaredField("id");
            idField.setAccessible(true);
            String id = (String) idField.get(this);
            
            java.lang.reflect.Field propertiesField = ComponentBuilder.class.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Object> properties = (Map<String, Object>) propertiesField.get(this);
            
            java.lang.reflect.Field stylesField = ComponentBuilder.class.getDeclaredField("styles");
            stylesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> styles = (Map<String, String>) stylesField.get(this);
            
            java.lang.reflect.Field childrenField = ComponentBuilder.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Object> children = (List<Object>) childrenField.get(this);
            
            java.lang.reflect.Method formatStyleMethod = ComponentBuilder.class.getDeclaredMethod("formatStylePropertyValue", String.class);
            formatStyleMethod.setAccessible(true);
            
            java.lang.reflect.Method formatPropMethod = ComponentBuilder.class.getDeclaredMethod("formatPropertyValue", String.class, String.class);
            formatPropMethod.setAccessible(true);
            
            StringBuilder builder = new StringBuilder();
            
            // Component header (no variables section)
            String indentStr = (String) indentMethod.invoke(this, componentIndent);
            builder.append(indentStr);
            builder.append(component);
            
            if (id != null) {
                builder.append(" #").append(id);
            }
            builder.append(" {\n");
            
            // Build Style map
            Map<String, Object> styleMap = new HashMap<>();
            String stylesFromProps = (String) properties.get("Styles");
            if (stylesFromProps != null) {
                String[] stylesArray = stylesFromProps.split(",");
                for (String style : stylesArray) {
                    style = style.trim();
                    if (style.isEmpty()) continue;
                    int colonIndex = style.indexOf(':');
                    if (colonIndex != -1) {
                        String key = style.substring(0, colonIndex).trim();
                        String value = style.substring(colonIndex + 1).trim();
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        styleMap.put(key, value);
                    }
                }
            }
            for (Map.Entry<String, String> entry : styles.entrySet()) {
                String value = entry.getValue();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                styleMap.put(entry.getKey(), value);
            }
            if (!styleMap.isEmpty()) {
                properties.put("Style", styleMap);
            }
            
            // Properties
            List<String> propertiesOutput = new ArrayList<>();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String value;
                if (entry.getValue() instanceof Map<?, ?>) {
                    value = "(\n";
                    List<String> valueContent = new ArrayList<>();
                    boolean isStyle = entry.getKey().equals("Style");
                    for (Map.Entry<?, ?> subEntry : ((Map<?, ?>) entry.getValue()).entrySet()) {
                        String subValue = isStyle ?
                            (String) formatStyleMethod.invoke(null, subEntry.getValue().toString()) :
                            (String) formatPropMethod.invoke(null, subEntry.getValue().toString(), entry.getKey());
                        valueContent.add((String) indentMethod.invoke(this, componentIndent + 2) + subEntry.getKey() + ": " + subValue);
                    }
                    value += String.join(",\n", valueContent) + "\n" + indentMethod.invoke(this, componentIndent + 1) + ")";
                } else {
                    value = (String) formatPropMethod.invoke(null, entry.getValue().toString(), entry.getKey());
                }
                propertiesOutput.add(entry.getKey() + ": " + value);
            }
            
            String propertiesContent = String.join(";\n" + indentMethod.invoke(this, componentIndent + 1), propertiesOutput);
            if (!propertiesContent.isBlank()) {
                builder.append(indentMethod.invoke(this, componentIndent + 1)).append(propertiesContent).append(";\n");
            }
            
            // Children - process recursively but avoid calling build() by using buildComponentWithoutVariables
            if (!children.isEmpty() && (!propertiesContent.isBlank() || !styles.isEmpty())) {
                builder.append("\n");
            }
            
            int childIndex = 0;
            for (Object child : children) {
                childIndex++;
                if (child instanceof String) {
                    builder.append(indentMethod.invoke(this, componentIndent + 1)).append(((String) child).trim()).append("\n");
                } else if (child instanceof HTMLCustomUITemplate) {
                    // For HTMLCustomUITemplate children, also avoid recursion
                    builder.append(((HTMLCustomUITemplate) child).buildComponentWithoutVariables()).append("\n");
                } else if (child instanceof ComponentBuilder) {
                    // For other ComponentBuilder children, we need to build them, but this could cause recursion
                    // The issue is that ComponentBuilder.build() may call methods on children that are HTMLCustomUITemplate
                    // For now, we'll need to handle this carefully - but children should not be HTMLCustomUITemplate instances
                    builder.append(((ComponentBuilder) child).build(this.getSettings())).append("\n");
                }
                if (childIndex < children.size()) {
                    builder.append("\n");
                }
            }
            
            builder.append(indentMethod.invoke(this, componentIndent)).append("}");
            return builder.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to build component without variables", e);
        }
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

    /**
     * Dynamic EventData class that stores values in a Map.
     * Used by buildEventDataCodec() to create a dynamic codec based on template variables.
     */
    public static class DynamicEventData {
        private final Map<String, Object> values = new HashMap<>();

        /**
         * Set a value for a variable.
         * @param name The variable name (without @ prefix).
         * @param value The value to set.
         */
        public void setValue(String name, Object value) {
            this.values.put(name, value);
        }

        /**
         * Get a value for a variable.
         * @param name The variable name (without @ prefix).
         * @return The value, or null if not found.
         */
        public Object getValue(String name) {
            return this.values.get(name);
        }

        /**
         * Get a value as string.
         * @param name The variable name (without @ prefix).
         * @return The value as string, or null if not found.
         */
        public String getString(String name) {
            Object value = this.values.get(name);
            return value != null ? value.toString() : null;
        }

        /**
         * Get a value as boolean.
         * @param name The variable name (without @ prefix).
         * @return The value as boolean, or false if not found or not a boolean.
         */
        public boolean getBoolean(String name) {
            Object value = this.values.get(name);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return false;
        }

        /**
         * Get all values as a map.
         * @return A copy of the values map.
         */
        public Map<String, Object> getAllValues() {
            return new HashMap<>(this.values);
        }
    }
}
