package com.machina.minterfacebuilder.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.concurrent.ConcurrentHashMap;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.registry.HComponentRegistry;

/**
 * Factory for creating ComponentBuilder instances from HTML tags.
 * Handles custom component registration and default tag mappings.
 */
public class ComponentFactory {
    /**
     * Registry of custom component tags.
     * Maps tag name to a factory function that creates a ComponentBuilder from attributes.
     */
    private static final Map<String, Function<Map<String, String>, ComponentBuilder>> customComponents = new ConcurrentHashMap<>();

    /**
     * Static initializer to register default custom components.
     */
    static {
        // Register QRCode component by default
        registerCustomTag("qrcode", com.machina.minterfacebuilder.util.customui.components.QRCodeComponent.class);
        
        // Register all H components automatically
        registerAllHComponents();
    }
    
    /**
     * Register all H components from HComponentRegistry.
     * This method automatically registers all 37 H components with ComponentFactory.
     */
    private static void registerAllHComponents() {
        Map<String, Class<? extends ComponentBuilder>> hComponents = HComponentRegistry.getComponentMap();
        for (Map.Entry<String, Class<? extends ComponentBuilder>> entry : hComponents.entrySet()) {
            registerCustomTag(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Register a custom component tag.
     * @param tagName The HTML tag name to register (case-insensitive).
     * @param factory A function that creates a ComponentBuilder from attributes.
     */
    public static void registerCustomTag(String tagName, Function<Map<String, String>, ComponentBuilder> factory) {
        if (tagName == null || factory == null) {
            throw new IllegalArgumentException("Tag name and factory cannot be null");
        }
        customComponents.put(tagName.toLowerCase(), factory);
    }

    /**
     * Register a custom component tag using a class that extends ComponentBuilder.
     * The class must have a constructor that accepts Map<String, String> attributes.
     * @param tagName The HTML tag name to register (case-insensitive).
     * @param componentClass The class that extends ComponentBuilder.
     */
    public static void registerCustomTag(String tagName, Class<? extends ComponentBuilder> componentClass) {
        if (tagName == null || componentClass == null) {
            throw new IllegalArgumentException("Tag name and component class cannot be null");
        }

        registerCustomTag(tagName, attributes -> {
            try {
                // Try to find a constructor that accepts Map<String, String>
                java.lang.reflect.Constructor<? extends ComponentBuilder> constructor =
                    componentClass.getConstructor(Map.class);
                return constructor.newInstance(attributes);
            } catch (Exception e) {
                // If no such constructor, try default constructor and set attributes manually
                try {
                    ComponentBuilder instance = componentClass.getConstructor().newInstance();
                    // Apply attributes to the instance if possible
                    // This is a simplified approach - full implementation would parse all attributes
                    return instance;
                } catch (Exception e2) {
                    throw new RuntimeException("Failed to create custom component instance: " + componentClass.getName(), e2);
                }
            }
        });
    }

    /**
     * Create a ComponentBuilder from an HTML tag name.
     * @param tagName The HTML tag name (lowercase).
     * @param tagNameOriginal The original HTML tag name (preserves case).
     * @param attributes The tag attributes.
     * @param commonPrefix The common prefix (e.g., "$C." or "$Common.") or null.
     * @param customAliases Map of custom aliases (e.g., "C" -> "../Common.ui").
     * @return The ComponentBuilder.
     */
    public static ComponentBuilder createFromTag(String tagName, String tagNameOriginal, Map<String, String> attributes, String commonPrefix, Map<String, String> customAliases) {
        // Check for custom component first
        Function<Map<String, String>, ComponentBuilder> customFactory = customComponents.get(tagName.toLowerCase());
        if (customFactory != null) {
            return customFactory.apply(attributes);
        }

        // If there's a common prefix, use it directly (supports both built-in and custom aliases)
        if (commonPrefix != null && !commonPrefix.isEmpty()) {
            // Remove the trailing dot from prefix and normalize
            String prefix = commonPrefix.substring(0, commonPrefix.length() - 1);
            
            // Check for built-in aliases ($C, $Common)
            if (prefix.equalsIgnoreCase("$C") || prefix.equalsIgnoreCase("$Common")) {
                // Use the original tag name to preserve PascalCase (e.g., TextButton)
                // Preserve the original prefix ($C or $Common)
                return ComponentBuilder.create(prefix + ".@" + tagNameOriginal);
            }
            
            // Check for custom aliases (e.g., $C from script, $Sounds, etc.)
            // Remove $ prefix to get alias name
            if (prefix.startsWith("$")) {
                String aliasName = prefix.substring(1);
                if (customAliases != null && customAliases.containsKey(aliasName)) {
                    // Custom alias found - use it to create component
                    // Format: $AliasName.@ComponentName (e.g., $C.@TextField)
                    return ComponentBuilder.create(prefix + ".@" + tagNameOriginal);
                }
            }
        }

        // Internal tags (start with underscore)
        if (tagName.startsWith("_")) {
            switch (tagName) {
                case "_img":
                    // Internal tag for pixel-by-pixel image rendering
                    String src = attributes.get("src");
                    if (src == null || src.isEmpty()) {
                        src = attributes.get("value");
                    }
                    if (src == null || src.isEmpty()) {
                        return ComponentBuilder.create("Group");
                    }

                    // Parse optional attributes
                    Map<String, Object> renderProperties = new java.util.HashMap<>();
                    String blockSize = attributes.get("blocksize");
                    if (blockSize != null && !blockSize.isEmpty()) {
                        try {
                            renderProperties.put("BlockSize", Integer.parseInt(blockSize));
                        } catch (NumberFormatException e) {
                            // Ignore invalid blockSize
                        }
                    }

                    String maxWidth = attributes.get("maxwidth");
                    if (maxWidth != null && !maxWidth.isEmpty()) {
                        try {
                            renderProperties.put("MaxWidth", Integer.parseInt(maxWidth));
                        } catch (NumberFormatException e) {
                            // Ignore invalid maxWidth
                        }
                    }

                    String maxHeight = attributes.get("maxheight");
                    if (maxHeight != null && !maxHeight.isEmpty()) {
                        try {
                            renderProperties.put("MaxHeight", Integer.parseInt(maxHeight));
                        } catch (NumberFormatException e) {
                            // Ignore invalid maxHeight
                        }
                    }

                    String skipWhite = attributes.get("skipwhite");
                    if (skipWhite != null && (skipWhite.equalsIgnoreCase("true") || skipWhite.equals("1"))) {
                        renderProperties.put("SkipWhite", true);
                    }

                    return com.machina.minterfacebuilder.util.ImageRenderer.renderImage(src, renderProperties);

                default:
                    // Unknown internal tag, return Group
                    return ComponentBuilder.create("Group");
            }
        }

        // Default tag mappings
        switch (tagName) {
            case "div":
            case "section":
            case "article":
            case "header":
            case "footer":
            case "nav":
            case "main":
                return ComponentBuilder.create("Group");

            case "button":
                return ComponentBuilder.create("Button");

            case "input":
                String type = attributes.getOrDefault("type", "text").toLowerCase();
                if (type.equals("password")) {
                    ComponentBuilder field = ComponentBuilder.create("$C.@TextField");
                    field.setProperty("PasswordChar", "*");
                    return field;
                } else if (type.equals("number")) {
                    return ComponentBuilder.create("$C.@NumberField");
                } else if (type.equals("color")) {
                    return ComponentBuilder.create("ColorPicker");
                } else if (type.equals("checkbox")) {
                    return ComponentBuilder.create("$C.@CheckBoxWithLabel");
                } else {
                    return ComponentBuilder.create("$C.@TextField");
                }

            case "textarea":
                return ComponentBuilder.create("$C.@MultilineTextField");

            case "select":
                return ComponentBuilder.create("$C.@DropdownBox");

            case "label":
                return ComponentBuilder.create("@MIBLabel");
            case "p":
                return ComponentBuilder.create("@MIBP");
            case "span":
                return ComponentBuilder.create("@MIBSpan");
            case "h1":
                return ComponentBuilder.create("@MIBH1");
            case "h2":
                return ComponentBuilder.create("@MIBH2");
            case "h3":
                return ComponentBuilder.create("@MIBH3");
            case "h4":
                return ComponentBuilder.create("@MIBH4");
            case "h5":
                return ComponentBuilder.create("@MIBH5");
            case "h6":
                return ComponentBuilder.create("@MIBH6");

            case "img":
                return ComponentBuilder.create("Image");

            case "ul":
            case "ol":
                return ComponentBuilder.create("Group");

            case "li":
                return ComponentBuilder.create("Label");

            default:
                // List of known unsupported HTML tags that should always throw exception
                // These tags don't have Hytale equivalents and should fail explicitly
                Set<String> unsupportedTags = Set.of(
                    "br", "hr", "video", "audio", "iframe", "canvas", "svg", 
                    "table", "thead", "tbody", "tr", "td", "th", "form", 
                    "script", "style", "meta", "link", "head", "body"
                );
                
                if (unsupportedTags.contains(tagName.toLowerCase())) {
                    // Known unsupported tag - always throw exception
                    throw new UnsupportedHTMLTagException(tagName);
                }
                
                // If no prefix and not a known HTML tag, try to use the tag name directly
                // as a Hytale component name (e.g., <TextField>, <TextButton>)
                // This allows using Hytale component names directly without aliases
                // The tag name is preserved in original case (e.g., TextField, not textfield)
                if (commonPrefix == null || commonPrefix.isEmpty()) {
                    // Only allow PascalCase names for direct component usage
                    // If the tag name doesn't start with uppercase, it's likely an unsupported HTML tag
                    if (tagNameOriginal.isEmpty() || !Character.isUpperCase(tagNameOriginal.charAt(0))) {
                        throw new UnsupportedHTMLTagException(tagName);
                    }
                    // Try to use as direct component name (preserve original case)
                    return ComponentBuilder.create(tagNameOriginal);
                }
                
                // Unknown tag with prefix - throw exception
                throw new UnsupportedHTMLTagException(tagName);
        }
    }
}
