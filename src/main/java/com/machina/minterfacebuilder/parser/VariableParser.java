package com.machina.minterfacebuilder.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.machina.minterfacebuilder.model.InterfaceVariable;
import com.machina.minterfacebuilder.parser.CSSStyleParser;

/**
 * Parser for extracting and substituting variables in HTML templates.
 * Variables are declared at the beginning of HTML in format: @Nome = "valor"
 * Variables can be referenced anywhere in the template using: @Variavel
 * <p>
 * <b>Supported value types:</b>
 * </p>
 * <ul>
 *   <li><b>Boolean</b>: @Var = true or @Var = false</li>
 *   <li><b>Color</b>: @Var = #FF0000 or @Var = "#FF0000"</li>
 *   <li><b>String</b>: @Var = "value" or @Var = 'value'</li>
 *   <li><b>Literal</b>: @Var = value (without quotes, used via InterfaceLiteral.of())</li>
 * </ul>
 */
public class VariableParser {
    /**
     * Pattern to match variable declarations with quoted values (@Nome = "valor").
     */
    private static final Pattern VARIABLE_PATTERN_QUOTED = Pattern.compile(
        "@([a-zA-Z_][a-zA-Z0-9]*)\\s*=\\s*[\"']([^\"']*)[\"']",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Pattern to match variable declarations with unquoted values (@Nome = valor).
     */
    private static final Pattern VARIABLE_PATTERN_UNQUOTED = Pattern.compile(
        "@([a-zA-Z_][a-zA-Z0-9]*)\\s*=\\s*([^\\s<>]+)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Pattern to match variable references (@Variavel).
     */
    private static final Pattern VARIABLE_REF_PATTERN = Pattern.compile(
        "@([a-zA-Z_][a-zA-Z0-9]*)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Detect the type of a variable value.
     * @param value The variable value.
     * @param isQuoted Whether the value was quoted in the declaration.
     * @return The InterfaceVariable with appropriate type.
     */
    private static InterfaceVariable detectVariableType(String value, boolean isQuoted) {
        if (isQuoted) {
            // Quoted values are strings or colors
            String trimmed = value.trim();
            
            // Check if it's a color (hex, rgb, or rgba)
            if (trimmed.startsWith("#") && (trimmed.length() == 7 || trimmed.length() == 9)) {
                // Color hex format (#RRGGBB or #RRGGBBAA)
                return InterfaceVariable.colorValue(trimmed);
            } else if (trimmed.toLowerCase().startsWith("rgb")) {
                // RGB or RGBA format - convert to HEX/RGB(opacity)
                String converted = CSSStyleParser.convertColorValue(trimmed);
                return InterfaceVariable.colorValue(converted);
            } else {
                // Regular string
                return InterfaceVariable.stringValue(value);
            }
        } else {
            // Unquoted values: boolean, literal, or color
            String trimmed = value.trim();
            String trimmedLower = trimmed.toLowerCase();
            
            if (trimmedLower.equals("true") || trimmedLower.equals("false")) {
                // Boolean
                return InterfaceVariable.booleanValue(trimmedLower);
            } else if (trimmed.startsWith("#") && (trimmed.length() == 7 || trimmed.length() == 9)) {
                // Color hex format (#RRGGBB or #RRGGBBAA)
                return InterfaceVariable.colorValue(trimmed);
            } else if (trimmedLower.startsWith("rgb")) {
                // RGB or RGBA format - convert to HEX/RGB(opacity)
                String converted = CSSStyleParser.convertColorValue(trimmed);
                return InterfaceVariable.colorValue(converted);
            } else {
                // Literal value
                return InterfaceVariable.literalValue(value);
            }
        }
    }

    /**
     * Pattern to match script tags with type="text/customui".
     */
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
        "<script\\s+type\\s*=\\s*[\"']text/customui[\"']\\s*>([\\s\\S]*?)</script>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Extract variable declarations and script content from HTML.
     * Variables are extracted from inside &lt;script type="text/customui"&gt;&lt;/script&gt; tags.
     * Variables are in the format: @Nome = "valor" or @Nome = valor
     * @param html The HTML string.
     * @param variables The map to store extracted variables (InterfaceVariable instances).
     * @param scriptContent Output parameter to store the script content (will be applied to root component).
     * @return The HTML string with script tags removed.
     */
    public static String extractVariables(String html, Map<String, InterfaceVariable> variables, StringBuilder scriptContent) {
        // Extract script tags with type="text/customui"
        String remainingHtml = html;
        Matcher scriptMatcher = SCRIPT_PATTERN.matcher(html);
        
        while (scriptMatcher.find()) {
            String scriptBody = scriptMatcher.group(1).trim();
            
            // Extract variables from script content
            extractVariablesFromScript(scriptBody, variables);
            
            // Append script content to output (for root component properties)
            if (scriptContent != null && !scriptBody.isEmpty()) {
                if (scriptContent.length() > 0) {
                    scriptContent.append("\n");
                }
                scriptContent.append(scriptBody);
            }
            
            // Remove script tag from HTML
            remainingHtml = remainingHtml.replace(scriptMatcher.group(0), "");
        }
        
        return remainingHtml;
    }

    /**
     * Extract variables from script content.
     * @param scriptContent The script content string.
     * @param variables The map to store extracted variables.
     * @throws IllegalArgumentException If the script content has invalid syntax.
     */
    private static void extractVariablesFromScript(String scriptContent, Map<String, InterfaceVariable> variables) {
        // Validate script syntax - check for malformed assignments
        validateScriptSyntax(scriptContent);
        
        int lastEnd = 0;

        // First, find all quoted variable declarations
        Matcher quotedMatcher = VARIABLE_PATTERN_QUOTED.matcher(scriptContent);
        while (quotedMatcher.find()) {
            String varName = quotedMatcher.group(1);
            String varValue = quotedMatcher.group(2);
            variables.put(varName, detectVariableType(varValue, true));
            lastEnd = Math.max(lastEnd, quotedMatcher.end());
        }

        // Then, find unquoted variable declarations
        Matcher unquotedMatcher = VARIABLE_PATTERN_UNQUOTED.matcher(scriptContent);
        while (unquotedMatcher.find()) {
            int varStart = unquotedMatcher.start();

            // Skip if this was already matched by quoted pattern
            if (varStart < lastEnd) {
                continue;
            }

            String varName = unquotedMatcher.group(1);
            String varValue = unquotedMatcher.group(2);

            // Check if this is not a quoted value (contains quotes)
            if (!varValue.contains("\"") && !varValue.contains("'")) {
                // Only add if not already added as quoted
                if (!variables.containsKey(varName)) {
                    variables.put(varName, detectVariableType(varValue, false));
                    lastEnd = Math.max(lastEnd, unquotedMatcher.end());
                }
            }
        }
    }

    /**
     * Validate script syntax to ensure all lines are properly formatted.
     * @param scriptContent The script content to validate.
     * @throws IllegalArgumentException If the script contains invalid syntax.
     */
    private static void validateScriptSyntax(String scriptContent) {
        if (scriptContent == null || scriptContent.trim().isEmpty()) {
            return;
        }

        String[] lines = scriptContent.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                // Skip empty lines and comments
                continue;
            }

            // Check for variable assignments (@Var = value)
            if (line.startsWith("@")) {
                // Must contain equals sign
                if (!line.contains("=")) {
                    throw new IllegalArgumentException("Invalid script syntax: variable assignment must contain '=' sign. Line: " + line);
                }
                
                // Extract variable name part
                int equalsIndex = line.indexOf('=');
                String varNamePart = line.substring(0, equalsIndex).trim();
                
                // Validate variable name format (@Name or @Name_123)
                if (!VARIABLE_REF_PATTERN.matcher(varNamePart).matches()) {
                    throw new IllegalArgumentException("Invalid script syntax: invalid variable name format. Line: " + line);
                }
            }
            
            // Check for quoted strings that are not closed
            int quoteCount = 0;
            boolean inDoubleQuote = false;
            boolean inSingleQuote = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) {
                    if (!inSingleQuote) {
                        inDoubleQuote = !inDoubleQuote;
                        quoteCount++;
                    }
                } else if (c == '\'' && (i == 0 || line.charAt(i - 1) != '\\')) {
                    if (!inDoubleQuote) {
                        inSingleQuote = !inSingleQuote;
                        quoteCount++;
                    }
                }
            }
            
            // If we have an odd number of quotes, a quote is not closed
            if (quoteCount % 2 != 0) {
                throw new IllegalArgumentException("Invalid script syntax: unclosed quotes. Line: " + line);
            }
        }
    }

    /**
     * Extract variable declarations from the beginning of HTML (legacy method for backward compatibility).
     * @deprecated Use {@link #extractVariables(String, Map, StringBuilder)} instead.
     * @param html The HTML string.
     * @param variables The map to store extracted variables (InterfaceVariable instances).
     * @return The HTML string with variables removed.
     */
    @Deprecated
    public static String extractVariables(String html, Map<String, InterfaceVariable> variables) {
        return extractVariables(html, variables, null);
    }

    /**
     * Substitute variable references in a string.
     * @param value The string value that may contain @Variavel references.
     * @param variables The variables map (InterfaceVariable instances).
     * @return The string with variable references substituted.
     */
    public static String substituteVariables(String value, Map<String, InterfaceVariable> variables) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        StringBuffer result = new StringBuffer();
        Matcher refMatcher = VARIABLE_REF_PATTERN.matcher(value);

        while (refMatcher.find()) {
            String varName = refMatcher.group(1);
            InterfaceVariable var = variables.get(varName);
            if (var != null) {
                String substitutionValue = var.getSubstitutionValue();
                refMatcher.appendReplacement(result, Matcher.quoteReplacement(substitutionValue));
            } else {
                // Variable not found, keep the reference
                refMatcher.appendReplacement(result, refMatcher.group(0));
            }
        }
        refMatcher.appendTail(result);

        return result.toString();
    }
}
