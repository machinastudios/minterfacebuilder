package com.machina.minterfacebuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.machina.minterfacebuilder.cache.FileWatcher;
import com.machina.minterfacebuilder.cache.TemplateCache;
import com.machina.minterfacebuilder.factory.ComponentFactory;
import com.machina.minterfacebuilder.model.HTMLCustomUITemplate;
import com.machina.minterfacebuilder.model.InterfaceVariable;
import com.machina.minterfacebuilder.parser.CSSStyleParser;
import com.machina.minterfacebuilder.parser.CustomUIScriptParser;
import com.machina.minterfacebuilder.parser.HTMLAttributeParser;
import com.machina.minterfacebuilder.parser.VariableParser;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

/**
 * HTML to Custom UI Interface Builder.
 * Parses HTML and converts it to Hytale Custom UI ComponentBuilder format.
 * Supports basic HTML tags, variables, custom components, and converts them to appropriate Custom UI components.
 */
public class InterfaceBuilder {
    /**
     * Pattern to match HTML tags with attributes.
     * Supports tags with $Common or $C prefix (e.g., $C.TextButton, $Common.TextButton).
     * Also supports tags with Common or C prefix (e.g., Common.PageOverlay, C.TextButton)
     * which will be converted to $Common.@PageOverlay and $C.@TextButton.
     * Supports self-closing tags in format <tag /> or <tag/>.
     */
    private static final Pattern TAG_PATTERN = Pattern.compile(
        "<(/?)((?:\\$?[a-zA-Z][a-zA-Z0-9]*)\\.)?([a-zA-Z@][a-zA-Z0-9]*)\\s*([^>]*?)(/?)\\s*>",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Pattern to match HTML comments.
     */
    private static final Pattern COMMENT_PATTERN = Pattern.compile(
        "<!--.*?-->",
        Pattern.DOTALL
    );

    /**
     * Pattern to match JavaScript script tags with type="text/javascript".
     */
    private static final Pattern JAVASCRIPT_SCRIPT_PATTERN = Pattern.compile(
        "<script\\s+type\\s*=\\s*[\"']text/javascript[\"']\\s*>([\\s\\S]*?)</script>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Pattern to match JavaScript import statements.
     * Format: import Common from "@/Common.ui";
     */
    private static final Pattern IMPORT_PATTERN = Pattern.compile(
        "import\\s+([a-zA-Z][a-zA-Z0-9]*)\\s+from\\s+[\"']([^\"']+)[\"'];?",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Parse HTML file from a path within an asset pack.
     * Uses AssetModule to find the asset pack containing the path and resolve it.
     * Uses cache to avoid re-parsing the same file.
     * <p>
     * This method will search through all registered asset packs to find the one
     * containing the specified path. If the path is found within an asset pack,
     * it will be resolved and parsed.
     * </p>
     * <p>
     * <b>Note:</b> For loading assets from mods/plugins, it's recommended to use
     * {@link com.machina.minterfacebuilder.util.PluginAsset#of PluginAsset.of()} to resolve
     * the path first, then use {@link #parse(Path)} instead of this method. This is because
     * {@code findAssetPackForPath()} may not work correctly for mod assets.
     * </p>
     * <p>
     * <b>Examples:</b>
     * </p>
     * <pre>
     * // Parse an asset from a path string (works for server assets)
     * HTMLCustomUITemplate template = InterfaceBuilder.parseAsset("ui/pages/login.html");
     * 
     * // Or from a Path object (works for server assets)
     * Path assetPath = Paths.get("ui/pages/login.html");
     * HTMLCustomUITemplate template2 = InterfaceBuilder.parseAsset(assetPath);
     * 
     * // For plugin/mod assets, use PluginAsset.of() with parse() instead:
     * Path pluginAssetPath = PluginAsset.of(pluginInstance, "Common/UI/Test.html");
     * if (pluginAssetPath != null) {
     *     HTMLCustomUITemplate template3 = InterfaceBuilder.parse(pluginAssetPath);
     * }
     * </pre>
     *
     * @param path The path to the HTML file within an asset pack (can be relative or absolute).
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     * @throws java.io.IOException If the file cannot be read or AssetModule is not available.
     * @see com.machina.minterfacebuilder.util.PluginAsset#of
     */
    public static HTMLCustomUITemplate parseAsset(String path) throws java.io.IOException {
        return parseAsset(Path.of(path), new HashMap<>());
    }

    /**
     * Parse HTML file from a path within an asset pack with provided variables.
     * Uses AssetModule to find the asset pack containing the path and resolve it.
     * Uses cache to avoid re-parsing the same file.
     *
     * @param path The path to the HTML file within an asset pack (can be relative or absolute).
     * @param variables Additional variables to use during parsing (will override template variables).
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     * @throws java.io.IOException If the file cannot be read or AssetModule is not available.
     */
    public static HTMLCustomUITemplate parseAsset(String path, Map<String, String> variables) throws java.io.IOException {
        return parseAsset(Path.of(path), variables);
    }


    /**
     * Parse HTML file from a Path within an asset pack.
     * Uses AssetModule to find the asset pack containing the path and resolve it.
     * Uses cache to avoid re-parsing the same file.
     *
     * @param path The Path to the HTML file within an asset pack (can be relative or absolute).
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     * @throws java.io.IOException If the file cannot be read or AssetModule is not available.
     */
    public static HTMLCustomUITemplate parseAsset(Path path) throws java.io.IOException {
        return parseAsset(path, new HashMap<>());
    }


    /**
     * Parse HTML file from a Path within an asset pack with provided variables.
     * Uses AssetModule to find the asset pack containing the path and resolve it.
     * Uses cache to avoid re-parsing the same file.
     *
     * @param path The Path to the HTML file within an asset pack (can be relative or absolute).
     * @param variables Additional variables to use during parsing (will override template variables).
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     * @throws java.io.IOException If the file cannot be read or AssetModule is not available.
     */
    public static HTMLCustomUITemplate parseAsset(Path path, Map<String, String> variables) throws java.io.IOException {
        // Resolve asset path using AssetModule
        Path resolvedPath = resolveAssetPath(path);
        if (resolvedPath == null) {
            // If AssetModule is not available or path not found in asset packs,
            // fall back to treating it as a regular file system path
            return parse(path, variables);
        }

        // Use the standard Path parsing method with resolved path
        return parse(resolvedPath, variables);
    }

    /**
     * Resolve a Path to an asset within an asset pack using AssetModule.
     * <p>
     * This method uses AssetModule to find which asset pack contains the specified path.
     * If found, it resolves the path within that asset pack and returns the resolved Path.
     * If not found or AssetModule is not available, returns null.
     * </p>
     *
     * @param path The Path to resolve (can be relative or absolute).
     * @return The resolved Path within the asset pack, or null if not found or AssetModule unavailable.
     */
    @Nullable
    private static Path resolveAssetPath(Path path) {
        try {
            // Try to use AssetModule to find the asset pack containing this path
            Object assetModule = Class.forName("com.hypixel.hytale.server.core.asset.AssetModule")
                .getMethod("get")
                .invoke(null);

            if (assetModule == null) {
                // AssetModule not initialized yet
                return null;
            }

            // Find the asset pack containing this path
            Object assetPack = assetModule.getClass()
                .getMethod("findAssetPackForPath", Path.class)
                .invoke(assetModule, path.toAbsolutePath().normalize());

            if (assetPack == null) {
                // Path not found in any asset pack
                return null;
            }

            // Get the root path of the asset pack
            Path packRoot = (Path) assetPack.getClass()
                .getMethod("getRoot")
                .invoke(assetPack);

            // Resolve the path relative to the asset pack root
            Path absolutePath = path.toAbsolutePath().normalize();
            if (absolutePath.startsWith(packRoot)) {
                // Path is already within the pack root, return as-is
                return absolutePath;
            } else {
                // Try to resolve as relative to pack root
                return packRoot.resolve(path).normalize();
            }
        } catch (Exception e) {
            // AssetModule not available or reflection failed
            // This is expected if running outside Hytale Server context
            return null;
        }
    }

    /**
     * Parse HTML file from Path and convert it to a HTMLCustomUITemplate.
     * Uses cache to avoid re-parsing the same file.
     * <p>
     * This method is the recommended way to parse assets from plugins/mods.
     * Use {@link com.machina.minterfacebuilder.util.PluginAsset#of PluginAsset.of()}
     * to resolve the path first, then pass it to this method.
     * </p>
     * <p>
     * <b>Example for plugin assets:</b>
     * </p>
     * <pre>
     * // In your JavaPlugin class
     * Path uiPath = PluginAsset.of(this, "Common/UI/Test.html");
     * if (uiPath != null) {
     *     HTMLCustomUITemplate template = InterfaceBuilder.parse(uiPath);
     *     // Use template...
     * }
     * </pre>
     *
     * @param path The Path to the HTML file.
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     * @throws java.io.IOException If the file cannot be read.
     * @see com.machina.minterfacebuilder.util.PluginAsset#of
     */
    public static HTMLCustomUITemplate parse(Path path) throws java.io.IOException {
        return parse(path, new HashMap<>());
    }

    /**
     * Parse HTML file from Path and convert it to a HTMLCustomUITemplate with provided variables.
     * Uses cache to avoid re-parsing the same file.
     * <p>
     * This method is the recommended way to parse assets from plugins/mods with variables.
     * Use {@link com.machina.minterfacebuilder.util.PluginAsset#of PluginAsset.of()}
     * to resolve the path first, then pass it to this method.
     * </p>
     * <p>
     * <b>Example for plugin assets:</b>
     * </p>
     * <pre>
     * // In your JavaPlugin class
     * Path uiPath = PluginAsset.of(this, "Common/UI/Test.html");
     * if (uiPath != null) {
     *     Map&lt;String, String&gt; vars = Map.of("title", "My Plugin");
     *     HTMLCustomUITemplate template = InterfaceBuilder.parse(uiPath, vars);
     *     // Use template...
     * }
     * </pre>
     *
     * @param path The Path to the HTML file.
     * @param variables Additional variables to use during parsing (will override template variables).
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     * @throws java.io.IOException If the file cannot be read.
     * @see com.machina.minterfacebuilder.util.PluginAsset#of
     */
    public static HTMLCustomUITemplate parse(Path path, Map<String, String> variables) throws java.io.IOException {
        // Note: Cache doesn't consider variables, so we don't use cache when variables are provided
        if (variables == null || variables.isEmpty()) {
            HTMLCustomUITemplate cached = TemplateCache.get(path);
            if (cached != null) {
                return cached;
            }
        }

        // Read file content
        String html = Files.readString(path);

        // Parse the HTML with variables
        HTMLCustomUITemplate result = parse(html, variables);

        // Store in cache only if no custom variables were provided
        if (variables == null || variables.isEmpty()) {
            TemplateCache.put(path, result);
        }

        return result;
    }

    /**
     * Parse HTML string and convert it to a HTMLCustomUITemplate.
     * @param html The HTML string to parse.
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     */
    public static HTMLCustomUITemplate parse(String html) {
        return parse(html, new HashMap<>());
    }

    /**
     * Parse HTML string and convert it to a HTMLCustomUITemplate with provided variables.
     * @param html The HTML string to parse.
     * @param variables Additional variables to use during parsing (will override template variables).
     * @return The HTMLCustomUITemplate representing the parsed HTML.
     */
    public static HTMLCustomUITemplate parse(String html, Map<String, String> variables) {
        // Remove HTML comments
        html = COMMENT_PATTERN.matcher(html).replaceAll("");

        // Trim whitespace
        html = html.trim();

        // Extract variables and script content from <script type="text/customui"> tags
        Map<String, InterfaceVariable> templateVariables = new HashMap<>();
        StringBuilder scriptContent = new StringBuilder();
        html = VariableParser.extractVariables(html, templateVariables, scriptContent);

        // Extract JavaScript imports from <script type="text/javascript"> tags
        // Format: import Common from "@/Common.ui"; → $Common = "../Common.ui";
        Map<String, String> javaScriptAliases = extractJavaScriptImports(html);
        html = removeJavaScriptScripts(html);

        // Extract custom aliases from script content (e.g., $C = "../Common.ui")
        Map<String, String> customAliases = new HashMap<>();
        if (scriptContent.length() > 0) {
            customAliases = CustomUIScriptParser.extractCustomAliases(scriptContent.toString());
        }
        // Merge JavaScript imports into custom aliases
        customAliases.putAll(javaScriptAliases);

        // Merge provided variables (they override template variables)
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String key = entry.getKey().startsWith("@") ? entry.getKey().substring(1) : entry.getKey();
                String value = entry.getValue();
                // Convert String to InterfaceVariable (assume string type)
                templateVariables.put(key, InterfaceVariable.stringValue(value));
            }
        }

        // Trim whitespace again after extracting script tags
        html = html.trim();

        // If empty, return empty Group
        ComponentBuilder componentBuilder;
        ParseContext context;
        if (html.isEmpty()) {
            componentBuilder = ComponentBuilder.create("Group");
            context = new ParseContext(html, templateVariables, customAliases);
        } else {
            // Parse the HTML with custom aliases
            context = new ParseContext(html, templateVariables, customAliases);
            componentBuilder = parseElement(context);
        }

        // Apply script content to root component if present
        // The script content is parsed and properties/aliases are applied to the root component
        if (scriptContent.length() > 0) {
            String scriptText = scriptContent.toString().trim();
            CustomUIScriptParser.applyScriptContent(componentBuilder, scriptText, templateVariables, customAliases);
        }

        // Extract used tags from context (created during parsing)
        Set<String> usedHtmlTags = context.usedHtmlTags;
        Set<String> usedAliases = context.usedAliases;
        
        // Create HTMLCustomUITemplate with variables and used HTML tags
        HTMLCustomUITemplate template = new HTMLCustomUITemplate(componentBuilder, templateVariables, usedHtmlTags);
        // Store used aliases for output generation
        template.setUsedAliases(usedAliases, customAliases);
        
        return template;
    }


    /**
     * Extract JavaScript imports from script tags and convert them to aliases.
     * Format: import Common from "@/Common.ui"; → $Common = "../Common.ui";
     * @param html The HTML string.
     * @return Map of alias names to their paths.
     */
    private static Map<String, String> extractJavaScriptImports(String html) {
        Map<String, String> aliases = new HashMap<>();
        Matcher scriptMatcher = JAVASCRIPT_SCRIPT_PATTERN.matcher(html);
        
        while (scriptMatcher.find()) {
            String scriptBody = scriptMatcher.group(1).trim();
            
            // Extract imports from script content
            Matcher importMatcher = IMPORT_PATTERN.matcher(scriptBody);
            while (importMatcher.find()) {
                String aliasName = importMatcher.group(1); // e.g., "Common"
                String filePath = importMatcher.group(2); // e.g., "@/Common.ui"
                
                // Convert @/ to ../ (relative path from Pages to parent directory)
                String normalizedPath = filePath.replace("@/", "../");
                
                aliases.put(aliasName, normalizedPath);
            }
        }
        
        return aliases;
    }

    /**
     * Remove JavaScript script tags from HTML.
     * @param html The HTML string.
     * @return The HTML string with JavaScript script tags removed.
     */
    private static String removeJavaScriptScripts(String html) {
        return JAVASCRIPT_SCRIPT_PATTERN.matcher(html).replaceAll("");
    }

    /**
     * Clear the cache.
     */
    public static void clearCache() {
        TemplateCache.clear();
    }

    /**
     * Remove a specific path from the cache.
     * @param path The Path to remove from cache.
     */
    public static void removeFromCache(Path path) {
        TemplateCache.remove(path);
    }

    /**
     * Start watching a file for changes and automatically reload it when modified.
     * <p>
     * This method monitors the specified file using the file system's watch service.
     * When the file is modified on disk, it will be automatically removed from the cache,
     * causing it to be reloaded from disk on the next parse call.
     * </p>
     * <p>
     * The file watcher runs in a background daemon thread and does not block the calling thread.
     * Multiple files can be watched simultaneously.
     * </p>
     * <p>
     * <b>Example:</b>
     * </p>
     * <pre>
     * Path templatePath = Path.of("templates/ui.html");
     * InterfaceBuilder.watchFileChanges(templatePath);
     *
     * // Parse the file (will be cached)
     * HTMLCustomUITemplate template = InterfaceBuilder.parse(templatePath);
     *
     * // Later, when the file is modified on disk, the cache will be cleared
     * // The next parse call will automatically reload the file
     * HTMLCustomUITemplate updatedTemplate = InterfaceBuilder.parse(templatePath);
     * </pre>
     * <p>
     * <b>Note:</b> This method must be called with an absolute path. Relative paths will be
     * converted to absolute paths using the current working directory.
     * </p>
     *
     * @param filePath The path to the file to watch. Must be an absolute path or will be converted to absolute.
     * @throws java.io.IOException If the file cannot be watched (e.g., file doesn't exist or is not accessible).
     * @throws IllegalArgumentException If the file path is null.
     */
    public static void watchFileChanges(Path filePath) throws java.io.IOException {
        FileWatcher.watchFile(filePath);
    }

    /**
     * Stop watching a file for changes.
     * <p>
     * This will stop monitoring the file for changes, but will not remove it from the cache.
     * The cached template will remain until explicitly removed or cleared.
     * </p>
     *
     * @param filePath The path to the file to stop watching.
     */
    public static void stopWatchingFile(Path filePath) {
        FileWatcher.stopWatching(filePath);
    }

    /**
     * Check if a file is currently being watched for changes.
     * @param filePath The path to check.
     * @return True if the file is being watched, false otherwise.
     */
    public static boolean isWatchingFile(Path filePath) {
        return FileWatcher.isWatching(filePath);
    }

    /**
     * Stop watching all files and shutdown the file watcher service.
     * <p>
     * This will stop all file watchers and shutdown the background thread.
     * The cache will not be cleared, but files will no longer be monitored for changes.
     * </p>
     * <p>
     * <b>Note:</b> This is typically called during application shutdown to clean up resources.
     * </p>
     */
    public static void stopAllWatchers() {
        FileWatcher.stopAll();
    }

    /**
     * Parse a single HTML element and its children.
     * @param context The parsing context.
     * @return The ComponentBuilder for the element.
     */
    private static ComponentBuilder parseElement(ParseContext context) {
        String html = context.html;
        int pos = context.position;

        // Skip whitespace
        while (pos < html.length() && Character.isWhitespace(html.charAt(pos))) {
            pos++;
        }

        // If we've reached the end, return null
        if (pos >= html.length()) {
            return null;
        }

        // If we're at a closing tag, return null (but don't update position - let caller handle it)
        if (html.charAt(pos) == '<' && pos + 1 < html.length() && html.charAt(pos + 1) == '/') {
            // Don't update position here - the caller needs to know we're at a closing tag
            // but also needs to collect any text before it
            return null;
        }

        // Find the opening tag
        Matcher tagMatcher = TAG_PATTERN.matcher(html);
        if (!tagMatcher.find(pos)) {
            // No tag found, treat as text content
            String text = html.substring(pos).trim();
            if (!text.isEmpty()) {
                ComponentBuilder label = ComponentBuilder.create("Label");
                label.setProperty("Text", text);
                context.position = html.length();
                return label;
            }
            return null;
        }

        // Check if it's a closing tag
        if (!tagMatcher.group(1).isEmpty()) {
            context.position = tagMatcher.end();
            return null;
        }

        String commonPrefix = tagMatcher.group(2);
        String tagNameOriginal = tagMatcher.group(3);
        String tagName = tagNameOriginal.toLowerCase();
        String attributesStr = tagMatcher.group(4);
        String selfClosingSlash = tagMatcher.group(5);
        int tagEnd = tagMatcher.end();

        // Normalize prefix: convert Common. to $Common. (ComponentFactory will add @ to tagName)
        if (commonPrefix != null && !commonPrefix.isEmpty()) {
            // Remove trailing dot
            String prefixWithoutDot = commonPrefix.substring(0, commonPrefix.length() - 1);
            
            // Track alias usage for output generation
            // Store original alias name before normalization
            String originalAlias = prefixWithoutDot;
            if (!prefixWithoutDot.startsWith("$")) {
                context.usedAliases.add(prefixWithoutDot); // Track Common or C
            } else {
                // Already has $, track without it
                context.usedAliases.add(prefixWithoutDot.substring(1)); // Track Common or C
            }
            
            // If prefix doesn't start with $, add it
            if (!prefixWithoutDot.startsWith("$")) {
                prefixWithoutDot = "$" + prefixWithoutDot;
            }
            
            // Reconstruct prefix with dot (ComponentFactory will handle @ prefix on tagName)
            commonPrefix = prefixWithoutDot + ".";
        }

        // Parse attributes and substitute variables
        Map<String, String> attributes = HTMLAttributeParser.parseAttributes(attributesStr, context.variables);

        // Track HTML tags that need aliases (h1-h6, span, p, label)
        // These tags map to Label components and should have aliases generated
        // Use lowercase to ensure consistency and prevent duplicates
        if (commonPrefix == null || commonPrefix.isEmpty()) {
            String tagNameLower = tagName.toLowerCase();
            // Check for h1-h6 (avoid regex to prevent stack overflow issues)
            boolean isHeading = tagNameLower.length() == 2 && tagNameLower.startsWith("h") && 
                tagNameLower.charAt(1) >= '1' && tagNameLower.charAt(1) <= '6';

            boolean isSpan = tagNameLower.equals("span");
            boolean isP = tagNameLower.equals("p");
            boolean isLabel = tagNameLower.equals("label");

            boolean isAnyTextTag = isHeading || isSpan || isP || isLabel;

            if (isAnyTextTag) {
                context.usedHtmlTags.add(tagNameLower);
            }
        }

        // Create the component based on tag name, using custom aliases if present
        ComponentBuilder component = ComponentFactory.createFromTag(tagName, tagNameOriginal, attributes, commonPrefix, context.customAliases);

        // Set ID if present (note: "id" is capitalized to "Id" by HTMLAttributeParser)
        String id = attributes.get("Id");
        if (id == null) {
            id = attributes.get("id"); // Fallback to lowercase (shouldn't happen)
        }
        if (id != null && !id.isEmpty()) {
            // Prohibit using MIBRoot as ID (reserved for root container)
            String idNormalized = id.trim();
            if (idNormalized.equalsIgnoreCase("MIBRoot")) {
                throw new IllegalArgumentException(
                    "ID 'MIBRoot' is reserved for the root container and cannot be used for HTML elements. " +
                    "Please use a different ID for element: <" + tagName + " id=\"" + id + "\">"
                );
            }

            component.setId(id);
        }

        // Set properties from attributes
        applyAttributesToComponent(component, attributes, tagName, context.variables);

        // Handle Group with Text attribute - create Label child instead
        // Group does not support Text property, so we need to create a Label child
        // Check for both "div" (HTML tag) and "group" (Hytale component name)
        Object groupTextValue = null;
        boolean isGroupComponent = tagName.equals("group") || tagName.equals("div") || 
                                 tagName.equals("section") || tagName.equals("article") ||
                                 tagName.equals("header") || tagName.equals("footer") ||
                                 tagName.equals("nav") || tagName.equals("main");
        if (isGroupComponent) {
            String textValue = attributes.get("Text");
            if (textValue == null) {
                textValue = attributes.get("text"); // Fallback to lowercase
            }
            // Check if Text was set as a property (from binding or attribute)
            if (textValue == null || textValue.isEmpty()) {
                Object textProperty = component.getProperty("Text");
                if (textProperty != null) {
                    groupTextValue = textProperty;
                }
            } else {
                groupTextValue = textValue;
            }

            // Remove Text from Group (it doesn't support it)
            if (groupTextValue != null) {
                component.getBuilderVariables().remove("Text");
                try {
                    java.lang.reflect.Field propertiesField = component.getClass().getDeclaredField("properties");
                    propertiesField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> properties = (Map<String, Object>) propertiesField.get(component);
                    properties.remove("Text");
                } catch (Exception e) {
                    // If reflection fails, ignore
                }
            }
        }

        // Check if it's a self-closing tag
        // Support both <tag /> and <tag/> formats
        boolean isSelfClosing = (!selfClosingSlash.isEmpty()) || 
                                attributesStr.trim().endsWith("/") ||
                                isSelfClosingTag(tagName);

        if (isSelfClosing) {
            context.position = tagEnd;
            return component;
        }

        // Find the closing tag
        int contentStart = tagEnd;
        int contentEnd = findClosingTag(html, tagName, contentStart);

        // If no closing tag found, treat as self-closing
        if (contentEnd == -1) {
            context.position = tagEnd;
            return component;
        }

        // Parse children
        context.position = contentStart;

        // If Group has Text attribute, create a Label child first
        if (isGroupComponent && groupTextValue != null) {
            ComponentBuilder labelChild = ComponentBuilder.create("Label");
            // If groupTextValue is a String, use it directly; otherwise convert to String
            String textStr = groupTextValue instanceof String ? (String) groupTextValue : groupTextValue.toString();
            labelChild.setProperty("Text", textStr);
            component.appendChild(labelChild);
        }

        // Special handling for <select> - collect options
        if (tagName.equals("select")) {
            List<String> options = new ArrayList<>();
            while (context.position < contentEnd) {
                int optionStart = context.position;
                int optionTag = html.indexOf("<option", optionStart);
                if (optionTag == -1 || optionTag >= contentEnd) {
                    break;
                }

                // Find closing tag
                int optionEnd = html.indexOf("</option>", optionTag);
                if (optionEnd == -1) {
                    optionEnd = html.indexOf("</select>", optionTag);
                    if (optionEnd == -1) {
                        optionEnd = contentEnd;
                    }
                } else {
                    optionEnd += 9; // "</option>".length()
                }

                // Parse option tag
                Matcher optionMatcher = TAG_PATTERN.matcher(html);
                if (optionMatcher.find(optionTag)) {
                    String optionAttributesStr = optionMatcher.group(4);
                    Map<String, String> optionAttributes = HTMLAttributeParser.parseAttributes(optionAttributesStr, context.variables);
                    
                    // Get option text (from value attribute or text content)
                    String optionValue = optionAttributes.get("Value");
                    if (optionValue == null) {
                        optionValue = optionAttributes.get("value");
                    }
                    if (optionValue == null || optionValue.isEmpty()) {
                        // Get text content between <option> and </option>
                        int optionTextStart = optionMatcher.end();
                        int optionTextEnd = html.indexOf("</option>", optionTextStart);
                        if (optionTextEnd == -1) {
                            optionTextEnd = optionEnd;
                        }
                        String optionText = html.substring(optionTextStart, optionTextEnd).trim();
                        optionValue = optionText.isEmpty() ? optionValue : optionText;
                    }
                    
                    if (optionValue != null && !optionValue.isEmpty()) {
                        options.add(optionValue);
                    }
                }

                context.position = optionEnd;
            }

            // Set options as a property (DropdownBox expects options as a list or array)
            if (!options.isEmpty()) {
                component.setProperty("Options", options);
            }
        } else {
            // Normal child parsing for other tags
            StringBuilder textContent = new StringBuilder();
            List<ComponentBuilder> childComponents = new ArrayList<>();
            
            while (context.position < contentEnd) {
                // Find next tag (opening or closing)
                int textStart = context.position;
                int nextTag = html.indexOf('<', textStart);
                if (nextTag == -1 || nextTag >= contentEnd) {
                    nextTag = contentEnd;
                }
                
                // Collect text before the next tag (this must happen BEFORE checking for closing tag)
                if (nextTag > textStart) {
                    String text = html.substring(textStart, nextTag).trim();
                    if (!text.isEmpty()) {
                        if (textContent.length() > 0) {
                            textContent.append(" ");
                        }
                        textContent.append(text);
                    }
                }
                
                // Move to the tag position
                context.position = nextTag;
                
                // If we've reached the end, break
                if (context.position >= contentEnd) {
                    break;
                }
                
                // Check if it's a closing tag - AFTER collecting any text before it
                if (html.charAt(context.position) == '<' && 
                    context.position + 1 < html.length() && 
                    html.charAt(context.position + 1) == '/') {
                    // Found closing tag - skip it and break
                    int closingTagEnd = html.indexOf('>', context.position);
                    if (closingTagEnd != -1) {
                        context.position = closingTagEnd + 1;
                    } else {
                        context.position = contentEnd;
                    }
                    break;
                }
                
                // Try to parse an element (opening tag)
                ComponentBuilder child = parseElement(context);
                if (child != null) {
                    childComponents.add(child);
                    component.appendChild(child);
                } else {
                    // parseElement returned null - might be end of content
                    if (context.position >= contentEnd) {
                        break;
                    }
                }
            }
            
            // If there's text content and no child components, apply text to component's Text property
            // Otherwise, if there are both text and components, text is ignored (components take priority)
            String textStr = textContent.toString().trim();
            if (!textStr.isEmpty() && childComponents.isEmpty()) {
                // Group does not support Text property - create Label child instead
                // Check for both "div" (HTML tag) and "group" (Hytale component name)
                // Reuse isGroupComponent already declared above
                if (isGroupComponent) {
                    ComponentBuilder labelChild = ComponentBuilder.create("Label");
                    labelChild.setProperty("Text", textStr);
                    component.appendChild(labelChild);
                } else {
                    // Only text, no components - apply directly to Text property
                    // Note: Do NOT substitute variables here - keep them as references (e.g., "@Title")
                    // Variables can be changed at runtime by the Hytale Custom UI system
                    component.setProperty("Text", textStr);
                }
            }
        }

        context.position = contentEnd + tagName.length() + 3; // </tagname>
        return component;
    }

    /**
     * Find the closing tag for a given tag name.
     * @param html The HTML string.
     * @param tagName The tag name to find the closing tag for.
     * @param startPos The position to start searching from.
     * @return The position of the closing tag, or -1 if not found.
     */
    private static int findClosingTag(String html, String tagName, int startPos) {
        int depth = 1;
        Matcher matcher = TAG_PATTERN.matcher(html);
        matcher.region(startPos, html.length());

        while (matcher.find()) {
            String currentTag = matcher.group(3).toLowerCase();
            boolean isClosing = !matcher.group(1).isEmpty();

            if (currentTag.equals(tagName)) {
                if (isClosing) {
                    depth--;
                    if (depth == 0) {
                        return matcher.start();
                    }
                } else {
                    depth++;
                }
            }
        }

        return -1;
    }

    /**
     * Apply default styles for heading elements (h1-h6).
     * Sets RenderBold to true and appropriate FontSize if not already specified.
     * @param component The component builder.
     * @param level The heading level (1-6).
     */
    private static void applyHeadingDefaults(ComponentBuilder component, int level) {
        // Default font sizes for headings (h1 = largest, h6 = smallest)
        int[] defaultFontSizes = {28, 24, 20, 18, 16, 14};
        
        // Check if RenderBold is already set in style
        boolean hasRenderBold = hasStyleProperty(component, "RenderBold");
        if (!hasRenderBold) {
            component.setStyle("RenderBold", "true");
        }
        
        // Check if FontSize is already set in style
        boolean hasFontSize = hasStyleProperty(component, "FontSize");
        if (!hasFontSize && level >= 1 && level <= 6) {
            component.setStyle("FontSize", String.valueOf(defaultFontSizes[level - 1]));
        }
    }

    /**
     * Check if a style property is already set in the component.
     * @param component The component builder.
     * @param propertyName The style property name.
     * @return True if the property is set, false otherwise.
     */
    private static boolean hasStyleProperty(ComponentBuilder component, String propertyName) {
        try {
            // Use reflection to access the private styles map
            java.lang.reflect.Field stylesField = component.getClass().getDeclaredField("styles");
            stylesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> styles = (Map<String, String>) stylesField.get(component);
            
            // Check if the property exists (case-insensitive check)
            for (String key : styles.keySet()) {
                if (key.equalsIgnoreCase(propertyName)) {
                    return true;
                }
            }
            
            // Also check in properties (in case it was set via setProperty with Style object)
            // This is a simplified check - in practice, Style would be a Map in properties
            return false;
        } catch (Exception e) {
            // If reflection fails, assume property is not set (safer to add defaults)
            return false;
        }
    }

    /**
     * Register a custom component tag.
     * Delegates to ComponentFactory.registerCustomTag.
     * @param tagName The HTML tag name to register (case-insensitive).
     * @param factory A function that creates a ComponentBuilder from attributes.
     */
    public static void registerCustomTag(String tagName, java.util.function.Function<Map<String, String>, ComponentBuilder> factory) {
        ComponentFactory.registerCustomTag(tagName, factory);
    }

    /**
     * Register a custom component tag using a class that extends ComponentBuilder.
     * Delegates to ComponentFactory.registerCustomTag.
     * @param tagName The HTML tag name to register (case-insensitive).
     * @param componentClass The class that extends ComponentBuilder.
     */
    public static void registerCustomTag(String tagName, Class<? extends ComponentBuilder> componentClass) {
        ComponentFactory.registerCustomTag(tagName, componentClass);
    }

    /**
     * Apply HTML attributes to a ComponentBuilder.
     * @param component The ComponentBuilder.
     * @param attributes The HTML attributes.
     * @param tagName The HTML tag name.
     * @param variables The variables map for substitution in binding attributes.
     */
    private static void applyAttributesToComponent(ComponentBuilder component, Map<String, String> attributes, String tagName, Map<String, InterfaceVariable> variables) {
        // Process binding attributes (starting with `:`) - these are CommonUI code bindings
        // Like Vue `:` binding: <span :text="@Title" /> means text is CommonUI code (variable substitution)
        Map<String, String> processedBindings = new HashMap<>();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (entry.getKey().startsWith(":")) {
                String attrName = entry.getKey().substring(1); // Remove `:` prefix
                String attrValue = entry.getValue();
                // Parse as CommonUI code (objects, variables, etc.)
                Object parsedValue = parseCommonUICodeFromAttribute(attrValue, variables);
                component.setProperty(attrName, parsedValue);
                processedBindings.put(attrName, attrValue);
            }
        }

        // Apply class as style reference (if needed)
        String className = attributes.get("Class");
        if (className == null) {
            className = attributes.get("class"); // Fallback
        }
        if (className != null && !className.isEmpty()) {
            // Could be used for style references
            // For now, we'll skip it or add as a comment
        }

        // Handle m-show and m-if (both convert to Visible property)
        // m-show="true" → Visible: true, m-show="false" → Visible: false
        // m-if="true" → Visible: true, m-if="false" → Visible: false
        // Note: HTMLAttributeParser capitalizes attributes using capitalizePropertyName,
        // which transforms "m-if" to "MIf" (removes dash, capitalizes each word)
        String mShow = null;
        String mIf = null;
        
        // Check all possible variations of m-show and m-if
        // capitalizePropertyName transforms: "m-show" → "MShow", "m-if" → "MIf"
        for (String key : attributes.keySet()) {
            // Normalize key for comparison (remove case and dashes)
            String normalizedKey = key.toLowerCase().replace("-", "").replace("_", "");
            if (normalizedKey.equals("mshow")) {
                mShow = attributes.get(key);
            } else if (normalizedKey.equals("mif")) {
                mIf = attributes.get(key);
            }
        }
        
        // Prefer m-show over m-if if both are present
        String visibilityAttr = mShow != null ? mShow : mIf;
        if (visibilityAttr != null && !visibilityAttr.isEmpty()) {
            String trimmed = visibilityAttr.trim();
            
            // If the value starts with @, it's a variable - use it directly without substitution
            if (trimmed.startsWith("@")) {
                // It's a variable reference (e.g., "@Enabled") - use it directly
                component.setProperty("Visible", trimmed);
            } else if (trimmed.startsWith("(") || trimmed.matches("^-?\\d+(\\.\\d+)?$")) {
                // It's CommonUI code or a number - parse and use directly
                Object parsed = parseCommonUICodeFromAttribute(trimmed, variables);
                component.setProperty("Visible", parsed);
            } else {
                // It's a literal boolean string - convert to boolean
                String lower = trimmed.toLowerCase();
                boolean isVisible = lower.equals("true") || lower.equals("1") || lower.equals("yes");
                component.setProperty("Visible", isVisible);
            }
        }

        // Apply style attribute (note: "style" is capitalized to "Style")
        String style = attributes.get("Style");
        if (style == null) {
            style = attributes.get("style"); // Fallback
        }
        if (style != null && !style.isEmpty()) {
            CSSStyleParser.applyStyle(component, style);
        }

        // Note: Default styles for headings (h1-h6) and paragraphs (p) are now in the aliases
        // (@MIBH1, @MIBH2, etc.), so we don't apply them here anymore.
        // Only styles explicitly specified in HTML will be applied.

        // Apply specific attributes based on tag type
        // Note: attributes are already capitalized (prop-name -> PropName)
        // Skip attributes that were already processed as bindings (starting with `:`)
        switch (tagName) {
            case "button":
                // Only apply Text if it wasn't already set as a binding attribute
                if (!processedBindings.containsKey("Text")) {
                    String buttonText = attributes.get("Value");
                    if (buttonText == null || buttonText.isEmpty()) {
                        buttonText = attributes.get("Text");
                    }
                    if (buttonText != null && !buttonText.isEmpty()) {
                        component.setProperty("Text", buttonText);
                    }
                }
                break;

            case "input":
                // Check input type first
                String inputType = attributes.getOrDefault("Type", "text").toLowerCase();
                if (inputType.equals("checkbox")) {
                    // Handle checkbox with label support
                    String label = attributes.get("Label");
                    if (label == null || label.isEmpty()) {
                        label = attributes.get("label"); // Try lowercase
                    }
                    if (label != null && !label.isEmpty()) {
                        component.setProperty("Text", label);
                    }

                    // Handle checked attribute
                    boolean isChecked = attributes.containsKey("Checked") || 
                                       attributes.containsKey("checked");
                    if (isChecked) {
                        String checkedValue = attributes.get("Checked");
                        if (checkedValue == null) {
                            checkedValue = attributes.get("checked");
                        }
                        if (checkedValue == null || checkedValue.isEmpty() || 
                            checkedValue.equalsIgnoreCase("true") || checkedValue.equals("1") ||
                            checkedValue.equalsIgnoreCase("checked")) {
                            component.setProperty("Value", true);
                        }
                    }
                } else {
                    // Handle text inputs, number inputs, etc.
                    String placeholder = attributes.get("Placeholder");
                    if (placeholder != null && !placeholder.isEmpty()) {
                        component.setProperty("PlaceholderText", placeholder);
                    }

                    String value = attributes.get("Value");
                    if (value != null && !value.isEmpty()) {
                        component.setProperty("Value", value);
                    }

                    String maxLength = attributes.get("Maxlength");
                    if (maxLength == null || maxLength.isEmpty()) {
                        maxLength = attributes.get("MaxLength"); // Try already capitalized
                    }
                    if (maxLength != null && !maxLength.isEmpty()) {
                        try {
                            component.setProperty("MaxLength", Integer.parseInt(maxLength));
                        } catch (NumberFormatException e) {
                            // Ignore invalid maxlength
                        }
                    }

                    // Handle readonly attribute
                    // In HTML, readonly attribute is boolean: if present (even without value), it's readonly
                    boolean isReadonly = attributes.containsKey("Readonly") || 
                                       attributes.containsKey("ReadOnly") || 
                                       attributes.containsKey("readonly");
                    if (isReadonly) {
                        // Check value if present - if explicitly false, don't set readonly
                        String readonlyValue = attributes.get("Readonly");
                        if (readonlyValue == null) {
                            readonlyValue = attributes.get("ReadOnly");
                        }
                        if (readonlyValue == null) {
                            readonlyValue = attributes.get("readonly");
                        }
                        // If value is explicitly "false", don't set readonly
                        if (readonlyValue == null || readonlyValue.isEmpty() || 
                            readonlyValue.equalsIgnoreCase("true") || readonlyValue.equals("1") ||
                            readonlyValue.equalsIgnoreCase("readonly")) {
                            component.setProperty("ReadOnly", true);
                        }
                    }
                }
                break;

            case "textarea":
                String placeholder = attributes.get("Placeholder");
                if (placeholder != null && !placeholder.isEmpty()) {
                    component.setProperty("PlaceholderText", placeholder);
                }

                String value = attributes.get("Value");
                if (value != null && !value.isEmpty()) {
                    component.setProperty("Value", value);
                }

                String maxLength = attributes.get("Maxlength");
                if (maxLength == null || maxLength.isEmpty()) {
                    maxLength = attributes.get("MaxLength"); // Try already capitalized
                }
                if (maxLength != null && !maxLength.isEmpty()) {
                    try {
                        component.setProperty("MaxLength", Integer.parseInt(maxLength));
                    } catch (NumberFormatException e) {
                        // Ignore invalid maxlength
                    }
                }

                // Handle readonly attribute for textarea
                boolean isReadonly = attributes.containsKey("Readonly") || 
                                   attributes.containsKey("ReadOnly") || 
                                   attributes.containsKey("readonly");
                if (isReadonly) {
                    String readonlyValue = attributes.get("Readonly");
                    if (readonlyValue == null) {
                        readonlyValue = attributes.get("ReadOnly");
                    }
                    if (readonlyValue == null) {
                        readonlyValue = attributes.get("readonly");
                    }
                    if (readonlyValue == null || readonlyValue.isEmpty() || 
                        readonlyValue.equalsIgnoreCase("true") || readonlyValue.equals("1") ||
                        readonlyValue.equalsIgnoreCase("readonly")) {
                        component.setProperty("ReadOnly", true);
                    }
                }
                break;

            case "label":
            case "p":
            case "span":
            case "h1":
            case "h2":
            case "h3":
            case "h4":
            case "h5":
            case "h6":
                // Text content will be set from child text nodes
                break;

            case "img":
                String src = attributes.get("Src");
                if (src != null && !src.isEmpty()) {
                    component.setProperty("Source", src);
                }

                String alt = attributes.get("Alt");
                if (alt != null && !alt.isEmpty()) {
                    component.setProperty("Tooltip", alt);
                }
                break;

            case "group":
            case "div":
            case "section":
            case "article":
            case "header":
            case "footer":
            case "nav":
            case "main":
                // Group does not support Text property directly
                // If Text is present in attributes, don't apply it here
                // It will be handled after applyAttributesToComponent in parseElement
                break;
        }
    }


    /**
     * Check if a tag is self-closing.
     * Only tags that are supported by Hytale Custom UI are considered self-closing.
     * @param tagName The tag name.
     * @return True if the tag is self-closing.
     */
    private static boolean isSelfClosingTag(String tagName) {
        return tagName.equals("img") || 
               tagName.equals("input");
    }

    /**
     * Parse CommonUI code from an attribute value (with `:` prefix).
     * Handles objects like (Left: 7), variables like @Title, and literals.
     * @param value The attribute value (may contain variables like @Title).
     * @param variables The variables map for substitution.
     * @return The parsed value (Map for objects, String for variables/literals).
     */
    private static Object parseCommonUICodeFromAttribute(String value, Map<String, InterfaceVariable> variables) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        value = value.trim();

        // First, substitute variables if value is a variable reference
        if (value.startsWith("@") && value.length() > 1) {
            String varName = value.substring(1);
            InterfaceVariable var = variables.get(varName);
            if (var != null) {
                // Return the substitution value from the variable
                return var.getSubstitutionValue();
            }
            // Variable not found, return as string literal
            return value;
        }

        // If it's an object like (Left: 7) or (Horizontal: 12, Left: 34)
        if (value.startsWith("(") && value.endsWith(")")) {
            return parseCommonUIObjectFromAttribute(value.substring(1, value.length() - 1), variables);
        }

        // Otherwise, treat as string literal (already substituted if needed)
        return value;
    }

    /**
     * Parse a CommonUI object from attribute content (without outer parentheses).
     * @param objectContent The object content string.
     * @param variables The variables map for substitution.
     * @return A Map representing the object.
     */
    private static Map<String, Object> parseCommonUIObjectFromAttribute(String objectContent, Map<String, InterfaceVariable> variables) {
        Map<String, Object> result = new HashMap<>();
        
        if (objectContent == null || objectContent.trim().isEmpty()) {
            return result;
        }

        // Split by comma, but respect nested parentheses
        List<String> pairs = new ArrayList<>();
        int depth = 0;
        int start = 0;
        
        for (int i = 0; i < objectContent.length(); i++) {
            char c = objectContent.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (c == ',' && depth == 0) {
                pairs.add(objectContent.substring(start, i).trim());
                start = i + 1;
            }
        }
        pairs.add(objectContent.substring(start).trim());

        // Parse each key:value pair
        for (String pair : pairs) {
            int colonIndex = pair.indexOf(':');
            if (colonIndex == -1) {
                continue;
            }

            String key = pair.substring(0, colonIndex).trim();
            String value = pair.substring(colonIndex + 1).trim();

            // Parse value - could be number, boolean, string, variable, or nested object
            Object parsedValue = parseCommonUIValueFromAttribute(value, variables);
            result.put(key, parsedValue);
        }

        return result;
    }

    /**
     * Parse a single CommonUI value from attribute (number, boolean, string, variable, or object).
     * @param value The value string (may contain @ variable references).
     * @param variables The variables map for substitution.
     * @return The parsed value.
     */
    private static Object parseCommonUIValueFromAttribute(String value, Map<String, InterfaceVariable> variables) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        value = value.trim();

        // Check if it's a variable reference (@VariableName)
        if (value.startsWith("@") && value.length() > 1) {
            String varName = value.substring(1);
            InterfaceVariable var = variables.get(varName);
            if (var != null) {
                return var.getSubstitutionValue();
            }
            // Variable not found, return as string literal
            return value;
        }

        // Check if it's a nested object
        if (value.startsWith("(") && value.endsWith(")")) {
            return parseCommonUIObjectFromAttribute(value.substring(1, value.length() - 1), variables);
        }

        // Check if it's a boolean
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }

        // Check if it's a number
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            // Not a number, return as string
            // Remove quotes if present
            if ((value.startsWith("\"") && value.endsWith("\"")) || 
                (value.startsWith("'") && value.endsWith("'"))) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
    }

    /**
     * Parsing context to track position in HTML string, variables, and custom aliases.
     */
    private static class ParseContext {
        String html;
        int position;
        Map<String, InterfaceVariable> variables;
        Map<String, String> customAliases;
        Set<String> usedHtmlTags;
        Set<String> usedAliases;

        ParseContext(String html, Map<String, InterfaceVariable> variables, Map<String, String> customAliases) {
            this.html = html;
            this.position = 0;
            this.variables = variables;
            this.customAliases = customAliases != null ? customAliases : new HashMap<>();
            this.usedHtmlTags = new java.util.HashSet<>();
            this.usedAliases = new java.util.HashSet<>();
        }
    }
}
