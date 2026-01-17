package com.machina.minterfacebuilder.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.machina.minterfacebuilder.model.InterfaceVariable;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

/**
 * Parser for script content inside &lt;script type="text/customui"&gt;&lt;/script&gt; tags.
 * <p>
 * This parser extracts variables, custom aliases, and applies properties/aliases to the root component.
 * Variables are in format: @Nome = "valor"
 * Custom aliases are in format: $Alias = "path" (e.g., $C = "../Common.ui")
 * Properties/aliases (non-variable, non-alias assignments) are applied directly to the root component.
 * </p>
 */
public class CustomUIScriptParser {
    /**
     * Pattern to match custom alias assignments (starting with $).
     * Format: $Alias = "path" or $Alias = path
     */
    private static final Pattern ALIAS_PATTERN_QUOTED = Pattern.compile(
        "^(\\$[a-zA-Z][a-zA-Z0-9]*)\\s*=\\s*[\"']([^\"']*)[\"']",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    /**
     * Pattern to match unquoted custom alias assignments.
     * Format: $Alias = path
     */
    private static final Pattern ALIAS_PATTERN_UNQUOTED = Pattern.compile(
        "^(\\$[a-zA-Z][a-zA-Z0-9]*)\\s*=\\s*([^\\s]+)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    /**
     * Pattern to match property/alias assignments (without @ or $ prefix).
     * Format: PropertyName = "value" or PropertyName = value
     */
    private static final Pattern PROPERTY_PATTERN_QUOTED = Pattern.compile(
        "^([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*[\"']([^\"']*)[\"']",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    /**
     * Pattern to match unquoted property/alias assignments.
     * Format: PropertyName = value
     */
    private static final Pattern PROPERTY_PATTERN_UNQUOTED = Pattern.compile(
        "^([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)$",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );


    /**
     * Parse script content and extract custom aliases.
     * Custom aliases are assignments starting with $ (e.g., $C = "../Common.ui").
     * @param scriptContent The script content string.
     * @return Map of alias names (without $) to their values.
     */
    public static Map<String, String> extractCustomAliases(String scriptContent) {
        Map<String, String> aliases = new HashMap<>();
        
        if (scriptContent == null || scriptContent.trim().isEmpty()) {
            return aliases;
        }

        // Parse quoted alias assignments
        Matcher quotedMatcher = ALIAS_PATTERN_QUOTED.matcher(scriptContent);
        while (quotedMatcher.find()) {
            String aliasName = quotedMatcher.group(1).substring(1); // Remove $ prefix
            String aliasValue = quotedMatcher.group(2);
            aliases.put(aliasName, aliasValue);
        }

        // Parse unquoted alias assignments
        Matcher unquotedMatcher = ALIAS_PATTERN_UNQUOTED.matcher(scriptContent);
        while (unquotedMatcher.find()) {
            String aliasName = unquotedMatcher.group(1).substring(1); // Remove $ prefix
            String aliasValue = unquotedMatcher.group(2).trim();
            // Only add if not already added as quoted
            if (!aliases.containsKey(aliasName)) {
                aliases.put(aliasName, aliasValue);
            }
        }

        return aliases;
    }

    /**
     * Apply script content to the root component.
     * <p>
     * This method extracts properties and aliases from the script content and applies them
     * directly to the root component. Only non-variable, non-custom-alias assignments
     * (not starting with @ or $) are treated as properties/aliases.
     * </p>
     * <p>
     * <b>Example:</b>
     * </p>
     * <pre>
     * &lt;script type="text/customui"&gt;
     * @Var = "value"              // Variable (not applied to root)
     * $C = "../Common.ui"         // Custom alias (not applied to root)
     * PropertyName = "value"      // Property applied to root
     * AliasName = "aliasValue"    // Alias applied to root
     * NumberProp = 123            // Numeric property
     * BoolProp = true             // Boolean property
     * &lt;/script&gt;
     * </pre>
     *
     * @param component The root component to apply properties to.
     * @param scriptContent The script content string.
     * @param variables The variables map (for reference, variables are not applied as properties).
     * @param customAliases The custom aliases map (for reference, aliases are not applied as properties).
     */
    public static void applyScriptContent(ComponentBuilder component, String scriptContent, Map<String, InterfaceVariable> variables, Map<String, String> customAliases) {
        if (scriptContent == null || scriptContent.trim().isEmpty()) {
            return;
        }

        // Parse quoted property assignments
        Matcher quotedMatcher = PROPERTY_PATTERN_QUOTED.matcher(scriptContent);
        while (quotedMatcher.find()) {
            String propertyName = quotedMatcher.group(1);
            String propertyValue = quotedMatcher.group(2);

            // Skip variables (starting with @) and custom aliases (starting with $) - they are handled separately
            if (propertyName.startsWith("@") || propertyName.startsWith("$") || 
                variables.containsKey(propertyName) || 
                (customAliases != null && customAliases.containsKey(propertyName))) {
                continue;
            }

            // Capitalize property name and apply
            String capitalizedName = capitalizePropertyName(propertyName);
            component.setProperty(capitalizedName, propertyValue);
        }

        // Parse unquoted property assignments
        Matcher unquotedMatcher = PROPERTY_PATTERN_UNQUOTED.matcher(scriptContent);
        while (unquotedMatcher.find()) {
            String propertyName = unquotedMatcher.group(1);
            String propertyValue = unquotedMatcher.group(2).trim();

            // Skip variables (starting with @) and custom aliases (starting with $) - they are handled separately
            if (propertyName.startsWith("@") || propertyName.startsWith("$") || 
                variables.containsKey(propertyName) || 
                (customAliases != null && customAliases.containsKey(propertyName))) {
                continue;
            }

            // Capitalize property name
            String capitalizedName = capitalizePropertyName(propertyName);
            // Treat as string literal (no parsing in script - `:` is only for HTML attributes)
            component.setProperty(capitalizedName, propertyValue);
        }
    }

    /**
     * Capitalize a property name following PascalCase rules.
     * <p>
     * Rules:
     * <ul>
     *   <li>prop-name → PropName (traços removidos, próxima letra maiúscula)</li>
     *   <li>propName → PropName (primeira letra maiúscula)</li>
     *   <li>prop → Prop (primeira letra maiúscula)</li>
     * </ul>
     * </p>
     * <p>
     * <b>Examples:</b>
     * </p>
     * <ul>
     *   <li>prop-name → PropName</li>
     *   <li>prop-name-example → PropNameExample</li>
     *   <li>propName → PropName</li>
     *   <li>prop → Prop</li>
     *   <li>alreadyCapitalized → AlreadyCapitalized</li>
     * </ul>
     *
     * @param propertyName The property name to capitalize.
     * @return The capitalized property name in PascalCase.
     */
    public static String capitalizePropertyName(String propertyName) {
        if (propertyName == null || propertyName.isEmpty()) {
            return propertyName;
        }

        // Handle kebab-case (prop-name)
        if (propertyName.contains("-")) {
            StringBuilder result = new StringBuilder();
            String[] parts = propertyName.split("-");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    // Capitalize first letter, lowercase rest
                    result.append(Character.toUpperCase(part.charAt(0)));
                    if (part.length() > 1) {
                        result.append(part.substring(1).toLowerCase());
                    }
                }
            }
            return result.toString();
        }

        // Handle camelCase or simple names
        // Capitalize first letter
        return Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
}
