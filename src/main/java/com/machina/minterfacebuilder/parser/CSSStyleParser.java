package com.machina.minterfacebuilder.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

/**
 * Strict CSS style parser for converting CSS properties to Custom UI component properties.
 * This parser is strict: it will throw an exception if it encounters any unsupported CSS property.
 */
public class CSSStyleParser {
    /**
     * Pattern to match rgb() color format: rgb(r, g, b) or rgb(r, g, b, a).
     */
    private static final Pattern RGB_PATTERN = Pattern.compile(
        "rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*(?:,\\s*([\\d.]+))?\\s*\\)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Pattern to match rgba() color format: rgba(r, g, b, a).
     */
    private static final Pattern RGBA_PATTERN = Pattern.compile(
        "rgba\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*([\\d.]+)\\s*\\)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Set of supported CSS properties (case-insensitive).
     */
    private static final Set<String> SUPPORTED_PROPERTIES = new HashSet<>();

    static {
        SUPPORTED_PROPERTIES.add("color");
        SUPPORTED_PROPERTIES.add("background-color");
        SUPPORTED_PROPERTIES.add("background");
        SUPPORTED_PROPERTIES.add("width");
        SUPPORTED_PROPERTIES.add("height");
        SUPPORTED_PROPERTIES.add("top");
        SUPPORTED_PROPERTIES.add("left");
        SUPPORTED_PROPERTIES.add("right");
        SUPPORTED_PROPERTIES.add("bottom");
        SUPPORTED_PROPERTIES.add("padding");
        SUPPORTED_PROPERTIES.add("margin");
        SUPPORTED_PROPERTIES.add("margin-top");
        SUPPORTED_PROPERTIES.add("margin-left");
        SUPPORTED_PROPERTIES.add("margin-right");
        SUPPORTED_PROPERTIES.add("margin-bottom");
        SUPPORTED_PROPERTIES.add("display");
        SUPPORTED_PROPERTIES.add("font-weight");
        SUPPORTED_PROPERTIES.add("font-size");
        SUPPORTED_PROPERTIES.add("font-name");
        SUPPORTED_PROPERTIES.add("text-align");
        SUPPORTED_PROPERTIES.add("text-transform");
        SUPPORTED_PROPERTIES.add("text-decoration");
        SUPPORTED_PROPERTIES.add("vertical-align");
        SUPPORTED_PROPERTIES.add("word-wrap");
        SUPPORTED_PROPERTIES.add("letter-spacing");
        SUPPORTED_PROPERTIES.add("text-outline-color");
    }

    /**
     * Exception thrown when an unsupported CSS property is encountered.
     */
    public static class UnsupportedCSSPropertyException extends RuntimeException {
        /**
         * The unsupported CSS property name.
         */
        private final String propertyName;

        /**
         * Constructor.
         * @param propertyName The unsupported CSS property name.
         */
        public UnsupportedCSSPropertyException(String propertyName) {
            super("Unsupported CSS property: " + propertyName);
            this.propertyName = propertyName;
        }

        /**
         * Get the unsupported CSS property name.
         * @return The property name.
         */
        public String getPropertyName() {
            return propertyName;
        }
    }

    /**
     * Parse and apply CSS style string to a ComponentBuilder.
     * @param component The ComponentBuilder to apply styles to.
     * @param styleStr The CSS style string (e.g., "color: red; width: 100px;").
     * @throws UnsupportedCSSPropertyException If an unsupported CSS property is encountered.
     */
    public static void applyStyle(ComponentBuilder component, String styleStr) {
        if (styleStr == null || styleStr.trim().isEmpty()) {
            return;
        }

        String[] styles = styleStr.split(";");
        for (String style : styles) {
            style = style.trim();
            if (style.isEmpty()) {
                continue;
            }

            int colonIndex = style.indexOf(':');
            if (colonIndex == -1) {
                // Malformed CSS property (no colon) - skip it
                continue;
            }

            String property = style.substring(0, colonIndex).trim().toLowerCase();
            String value = style.substring(colonIndex + 1).trim();

            // Check if property is supported
            if (!SUPPORTED_PROPERTIES.contains(property)) {
                throw new UnsupportedCSSPropertyException(property);
            }

            // Apply the property
            applyProperty(component, property, value);
        }
    }

    /**
     * Apply a single CSS property to a ComponentBuilder.
     * @param component The ComponentBuilder.
     * @param property The CSS property name (lowercase).
     * @param value The CSS property value.
     */
    private static void applyProperty(ComponentBuilder component, String property, String value) {
        switch (property) {
            case "color":
                // Convert color value (RGB/RGBA to HEX/RGB(opacity))
                String convertedColor = convertColorValue(value);
                component.setStyle("TextColor", convertedColor);
                break;

            case "background-color":
            case "background":
                // Parse background value (could be color or url())
                String backgroundValue = parseBackgroundValue(value);
                if (backgroundValue != null && !backgroundValue.isEmpty()) {
                    // Convert color if it's not a URL
                    if (!backgroundValue.toLowerCase().startsWith("http") && 
                        !backgroundValue.toLowerCase().startsWith("/") &&
                        !backgroundValue.contains("://")) {
                        backgroundValue = convertColorValue(backgroundValue);
                    }
                    component.setProperty("Background", backgroundValue);
                }
                break;

            case "width":
                try {
                    int widthValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Width", widthValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid width
                }
                break;

            case "height":
                try {
                    int heightValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Height", heightValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid height
                }
                break;

            case "top":
                try {
                    int topValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Top", topValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid top
                }
                break;

            case "left":
                try {
                    int leftValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Left", leftValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid left
                }
                break;

            case "right":
                try {
                    int rightValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Right", rightValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid right
                }
                break;

            case "bottom":
                try {
                    int bottomValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Bottom", bottomValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid bottom
                }
                break;

            case "padding":
                // Parse padding (simplified - only supports single value)
                try {
                    int paddingValue = parseSize(value);
                    Map<String, Object> padding = new HashMap<>();
                    padding.put("Vertical", paddingValue);
                    padding.put("Horizontal", paddingValue);
                    component.setProperty("Padding", padding);
                } catch (NumberFormatException e) {
                    // Ignore invalid padding
                }
                break;

            case "margin":
                // Margin is not directly supported in Custom UI, but it's in the supported list
                // so we don't throw an exception - just skip it
                break;

            case "margin-top":
                // Map margin-top to Anchor.Top
                try {
                    int marginTopValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Top", marginTopValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid margin-top
                }
                break;

            case "margin-left":
                // Map margin-left to Anchor.Left
                try {
                    int marginLeftValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Left", marginLeftValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid margin-left
                }
                break;

            case "margin-right":
                // Map margin-right to Anchor.Right
                try {
                    int marginRightValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Right", marginRightValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid margin-right
                }
                break;

            case "margin-bottom":
                // Map margin-bottom to Anchor.Bottom
                try {
                    int marginBottomValue = parseSize(value);
                    Map<String, Object> anchor = component.getProperty("Anchor");
                    if (anchor == null) {
                        anchor = new HashMap<>();
                    }
                    anchor.put("Bottom", marginBottomValue);
                    component.setProperty("Anchor", anchor);
                } catch (NumberFormatException e) {
                    // Ignore invalid margin-bottom
                }
                break;

            case "display":
                if (value.equals("none")) {
                    component.setProperty("Visible", false);
                } else {
                    // Any other display value means visible
                    component.setProperty("Visible", true);
                }
                break;

            case "font-weight":
                if (value.equals("bold") || value.equals("700") || value.equals("800") || value.equals("900")) {
                    component.setStyle("RenderBold", "true");
                }
                break;

            case "font-size":
                // Parse font size (e.g., "16px", "1.2em", "12")
                try {
                    int fontSizeValue = parseSize(value);
                    component.setStyle("FontSize", String.valueOf(fontSizeValue));
                } catch (NumberFormatException e) {
                    // Ignore invalid font-size
                }
                break;

            case "font-name":
                // Font family name (e.g., "Default", "Secondary")
                component.setStyle("FontName", value);
                break;

            case "text-align":
                // Horizontal alignment: left/start -> Start, center -> Center, right/end -> End
                String normalizedAlign = value.toLowerCase().trim();
                if (normalizedAlign.equals("center")) {
                    component.setStyle("HorizontalAlignment", "Center");
                } else if (normalizedAlign.equals("right") || normalizedAlign.equals("end")) {
                    component.setStyle("HorizontalAlignment", "End");
                } else if (normalizedAlign.equals("left") || normalizedAlign.equals("start")) {
                    component.setStyle("HorizontalAlignment", "Start");
                }
                break;

            case "text-transform":
                // Text transformation: uppercase -> RenderUppercase
                if (value.equals("uppercase")) {
                    component.setStyle("RenderUppercase", "true");
                } else if (value.equals("none") || value.equals("normal")) {
                    component.setStyle("RenderUppercase", "false");
                }
                break;

            case "text-decoration":
                // Text decoration: underline -> RenderUnderlined
                if (value.contains("underline")) {
                    component.setStyle("RenderUnderlined", "true");
                } else if (value.equals("none") || value.equals("normal")) {
                    component.setStyle("RenderUnderlined", "false");
                }
                break;

            case "vertical-align":
                // Vertical alignment: top/start -> Start, middle/center -> Center, bottom/end -> End
                String normalizedVerticalAlign = value.toLowerCase().trim();
                if (normalizedVerticalAlign.equals("center") || normalizedVerticalAlign.equals("middle")) {
                    component.setStyle("VerticalAlignment", "Center");
                } else if (normalizedVerticalAlign.equals("bottom") || normalizedVerticalAlign.equals("end")) {
                    component.setStyle("VerticalAlignment", "End");
                } else if (normalizedVerticalAlign.equals("top") || normalizedVerticalAlign.equals("start")) {
                    component.setStyle("VerticalAlignment", "Start");
                }
                break;

            case "word-wrap":
            case "overflow-wrap":
                // Word wrapping
                if (value.equals("break-word") || value.equals("break") || value.equals("wrap")) {
                    component.setStyle("Wrap", "true");
                } else if (value.equals("normal") || value.equals("nowrap")) {
                    component.setStyle("Wrap", "false");
                }
                break;

            case "letter-spacing":
                // Letter spacing (e.g., "2px", "0.5em", "2")
                try {
                    int letterSpacingValue = parseSize(value);
                    component.setStyle("LetterSpacing", String.valueOf(letterSpacingValue));
                } catch (NumberFormatException e) {
                    // Ignore invalid letter-spacing
                }
                break;

            case "text-outline-color":
                // Text outline color - convert RGB/RGBA to HEX/RGB(opacity)
                String convertedOutlineColor = convertColorValue(value);
                component.setStyle("OutlineColor", convertedOutlineColor);
                break;
        }
    }

    /**
     * Parse a size value (e.g., "100px", "50%", "100").
     * @param sizeStr The size string.
     * @return The parsed size as an integer.
     * @throws NumberFormatException If the size cannot be parsed.
     */
    private static int parseSize(String sizeStr) {
        sizeStr = sizeStr.trim().toLowerCase();

        // Remove units (px, em, etc.) - Custom UI typically uses pixels
        if (sizeStr.endsWith("px")) {
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("em")) {
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
            // Convert em to px (approximate: 1em = 16px)
            double emValue = Double.parseDouble(sizeStr);
            return (int) (emValue * 16);
        } else if (sizeStr.endsWith("%")) {
            // Percentage - for now, return a default value
            // In a real implementation, you'd need to know the parent size
            sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            double percentValue = Double.parseDouble(sizeStr);
            return (int) (percentValue * 10); // Approximate conversion
        }

        return Integer.parseInt(sizeStr);
    }

    /**
     * Parse background value (supports color or url()).
     * @param value The background value string.
     * @return The parsed background value.
     */
    private static String parseBackgroundValue(String value) {
        value = value.trim();

        // Check if it's a url()
        if (value.toLowerCase().startsWith("url(")) {
            // Extract URL from url(...)
            int start = value.indexOf('(') + 1;
            int end = value.lastIndexOf(')');
            if (start > 0 && end > start) {
                String url = value.substring(start, end).trim();
                // Remove quotes if present
                if ((url.startsWith("\"") && url.endsWith("\"")) ||
                    (url.startsWith("'") && url.endsWith("'"))) {
                    url = url.substring(1, url.length() - 1);
                }
                return url;
            }
        }

        // Otherwise, treat as color and convert if needed
        return convertColorValue(value);
    }

    /**
     * Convert color values from RGB/RGBA format to HEX or RGB(opacity) format.
     * <p>
     * Supported input formats:
     * - rgb(r, g, b) → #RRGGBB
     * - rgba(r, g, b, a) → #RRGGBB(opacity) where opacity is 0-1 (alpha/255)
     * - #RRGGBB or #RRGGBBAA → returned as-is (already in correct format)
     * - @VariableName → returned as-is (variable reference, resolved at runtime)
     * </p>
     * <p>
     * <b>Examples:</b>
     * </p>
     * <ul>
     *   <li>rgb(255, 0, 0) → #FF0000</li>
     *   <li>rgba(255, 0, 0, 0.5) → #FF0000(0.5)</li>
     *   <li>rgba(255, 0, 0, 128) → #FF0000(0.5) (alpha 128 = 0.5 opacity)</li>
     *   <li>#FF0000 → #FF0000 (no conversion needed)</li>
     *   <li>@ButtonColor → @ButtonColor (variable reference)</li>
     * </ul>
     * <p>
     * <b>Note:</b> Color names like "white", "red", "blue", etc. are NOT supported
     * and will throw an {@link IllegalArgumentException}. Only hex colors, rgb/rgba,
     * or variables are accepted.
     * </p>
     *
     * @param colorValue The color value string.
     * @return The converted color value (HEX or RGB(opacity) or variable reference).
     * @throws IllegalArgumentException If the color format is invalid (e.g., color names like "white").
     */
    public static String convertColorValue(String colorValue) {
        if (colorValue == null || colorValue.trim().isEmpty()) {
            return colorValue;
        }

        String trimmed = colorValue.trim();

        // Check if it's a hex color
        if (trimmed.startsWith("#")) {
            // Expand 3-digit hex to 6-digit (#RGB -> #RRGGBB)
            if (trimmed.length() == 4) {
                return "#" + trimmed.charAt(1) + trimmed.charAt(1) + 
                        trimmed.charAt(2) + trimmed.charAt(2) + 
                        trimmed.charAt(3) + trimmed.charAt(3);
            }
            // Already 6 or 8 digits, return as-is
            if (trimmed.length() == 7 || trimmed.length() == 9) {
                return trimmed;
            }
        }

        // Try to match rgba() format first (more specific)
        Matcher rgbaMatcher = RGBA_PATTERN.matcher(trimmed);
        if (rgbaMatcher.matches()) {
            int r = Integer.parseInt(rgbaMatcher.group(1));
            int g = Integer.parseInt(rgbaMatcher.group(2));
            int b = Integer.parseInt(rgbaMatcher.group(3));
            String alphaStr = rgbaMatcher.group(4);
            
            // Convert to hex
            String hex = String.format("#%02X%02X%02X", 
                Math.min(255, Math.max(0, r)),
                Math.min(255, Math.max(0, g)),
                Math.min(255, Math.max(0, b))
            );

            // Parse alpha (could be 0-1 or 0-255)
            double alpha = Double.parseDouble(alphaStr);
            if (alpha > 1.0) {
                // Alpha is 0-255, convert to 0-1
                alpha = alpha / 255.0;
            }
            
            // Format opacity with 2 decimal places
            return hex + String.format("(%.2f)", alpha).replaceAll("0+$", "").replaceAll("\\.$", ".0");
        }

        // Try to match rgb() format
        Matcher rgbMatcher = RGB_PATTERN.matcher(trimmed);
        if (rgbMatcher.matches()) {
            int r = Integer.parseInt(rgbMatcher.group(1));
            int g = Integer.parseInt(rgbMatcher.group(2));
            int b = Integer.parseInt(rgbMatcher.group(3));
            
            // Convert to hex (no alpha, so full opacity)
            return String.format("#%02X%02X%02X", 
                Math.min(255, Math.max(0, r)),
                Math.min(255, Math.max(0, g)),
                Math.min(255, Math.max(0, b))
            );
        }

        // Check if it's a variable reference (starts with @)
        if (trimmed.startsWith("@")) {
            // Variable reference - return as-is (will be resolved at runtime)
            return trimmed;
        }

        // If we reach here, it's not a valid color format (not hex, not rgb/rgba, not variable)
        // Throw an exception - only hex colors, rgb/rgba, or variables are supported
        throw new IllegalArgumentException(
            "Invalid color format: '" + colorValue + "'. " +
            "Only hex colors (#RRGGBB), rgb(r, g, b), rgba(r, g, b, a), or variables (@VariableName) are supported. " +
            "Color names like 'white', 'red', etc. are not supported."
        );
    }
}
