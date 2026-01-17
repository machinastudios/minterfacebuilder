package com.machina.minterfacebuilder.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.machina.minterfacebuilder.model.InterfaceVariable;
import com.machina.minterfacebuilder.parser.CustomUIScriptParser;

/**
 * Parser for HTML attributes.
 * Parses attributes from HTML attribute strings and substitutes variables.
 * <p>
 * <b>Internationalization (i18n) Support:</b>
 * </p>
 * <p>
 * Values starting with <code>%</code> are treated as literal i18n paths and are not
 * processed for variable substitution. These values are used for internationalization
 * and should be passed directly to the component without modification.
 * </p>
 * <p>
 * <b>Example:</b>
 * </p>
 * <pre>
 * &lt;button text="%ui.button.submit" /&gt;
 * &lt;label text="%messages.welcome" /&gt;
 * </pre>
 * <p>
 * In the examples above, <code>%ui.button.submit</code> and <code>%messages.welcome</code>
 * are i18n paths that will be resolved by the i18n system at runtime. They are not
 * processed for variable substitution and are passed as-is to the component.
 * </p>
 */
public class HTMLAttributeParser {
    /**
     * Pattern to match attribute="value", attribute='value', or :attribute="value" (dynamic binding).
     * The `:` prefix indicates CommonUI code binding (like Vue `:` binding).
     */
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
        "(:?)([a-zA-Z-]+)\\s*=\\s*[\"']([^\"']*)[\"']",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Pattern to match standalone attributes (without values).
     */
    private static final Pattern STANDALONE_ATTR_PATTERN = Pattern.compile(
        "([a-zA-Z-]+)(?=\\s|$)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Parse attributes from an attribute string and substitute variables.
     * <p>
     * Values starting with <code>%</code> are treated as literal i18n paths and are
     * not processed for variable substitution. This allows i18n paths to be passed
     * directly to components without modification.
     * </p>
     * <p>
     * <b>Example:</b>
     * </p>
     * <pre>
     * // i18n path - not processed for variables
     * text="%ui.button.submit"
     *
     * // Regular value - variables are substituted
     * text="@ButtonText"
     * </pre>
     *
     * @param attributesStr The attribute string.
     * @param variables The variables map for substitution.
     * @return A map of attribute names to values.
     */
    public static Map<String, String> parseAttributes(String attributesStr, Map<String, InterfaceVariable> variables) {
        Map<String, String> attributes = new HashMap<>();

        // Parse quoted attributes
        Matcher attrMatcher = ATTRIBUTE_PATTERN.matcher(attributesStr);
        while (attrMatcher.find()) {
            String hasColon = attrMatcher.group(1);
            String key = attrMatcher.group(2);
            String value = attrMatcher.group(3);

            // Capitalize attribute name (prop-name -> PropName, propName -> PropName, prop -> Prop)
            String capitalizedKey = CustomUIScriptParser.capitalizePropertyName(key);

            // If attribute starts with `:`, it's a dynamic binding (CommonUI code)
            // Store a marker to indicate this is a binding attribute
            if (":".equals(hasColon)) {
                // Mark as binding attribute (we'll handle the parsing in InterfaceBuilder)
                attributes.put(":" + capitalizedKey, value);
            } else {
                // Regular attribute
                // Check if value starts with % (i18n path) or @ (variable) - treat as-is without substitution
                // Variables starting with @ must be kept as references (can be changed at runtime)
                // i18n paths starting with % must be kept as-is (resolved by Hytale's i18n system)
                if (value != null && (value.startsWith("%") || value.startsWith("@"))) {
                    // i18n path or variable reference - use as-is without variable substitution
                    attributes.put(capitalizedKey, value);
                } else {
                    // Regular value - do NOT substitute variables here
                    // Variables in attribute values are kept as references (e.g., "@Title", "@ButtonColor")
                    // They will be resolved by the Hytale Custom UI system at runtime
                    attributes.put(capitalizedKey, value);
                }
            }
        }

        // Parse standalone attributes (like checked, disabled, etc.)
        Matcher standaloneMatcher = STANDALONE_ATTR_PATTERN.matcher(attributesStr);
        while (standaloneMatcher.find()) {
            String attr = standaloneMatcher.group(1);
            // Capitalize attribute name
            String capitalizedAttr = CustomUIScriptParser.capitalizePropertyName(attr);
            if (!attributes.containsKey(capitalizedAttr) && !attr.equals("/")) {
                attributes.put(capitalizedAttr, "true");
            }
        }

        return attributes;
    }
}
