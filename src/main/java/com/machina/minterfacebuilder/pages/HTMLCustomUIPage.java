package com.machina.minterfacebuilder.pages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.machina.minterfacebuilder.InterfaceBuilder;
import com.machina.minterfacebuilder.model.HTMLCustomUITemplate;
import com.machina.minterfacebuilder.util.customui.ComponentBuilderSettings;

/**
 * A Custom UI Page that builds its UI directly from HTML using the InterfaceBuilder parser.
 * <p>
 * This class extends {@link InteractiveCustomUIPage} and automatically converts HTML content
 * to Custom UI format during the build process.
 * </p>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>
 * // Simple usage with HTML string
 * HTMLCustomUIPage page = new HTMLCustomUIPage(playerRef, CustomPageLifetime.CanDismiss, eventDataCodec) {
 *     {@literal @}Override
 *     protected String getHTML() {
 *         return """
 *             &lt;script type="text/customui"&gt;
 *             @Title = "My Page"
 *             &lt;/script&gt;
 *             
 *             &lt;div id="container" style="padding: 20"&gt;
 *                 &lt;h1 id="title"&gt;@Title&lt;/h1&gt;
 *                 &lt;button id="button" text="Click Me"&gt;&lt;/button&gt;
 *             &lt;/div&gt;
 *             """;
 *     }
 * };
 * 
 * // Usage with HTML file
 * HTMLCustomUIPage page = HTMLCustomUIPage.fromFile(playerRef, CustomPageLifetime.CanDismiss, eventDataCodec, Path.of("pages/my-page.html"));
 * 
 * // Usage with HTML and variables
 * Map&lt;String, String&gt; variables = new HashMap&lt;&gt;();
 * variables.put("Title", "Custom Title");
 * HTMLCustomUIPage page = HTMLCustomUIPage.fromHTML(playerRef, CustomPageLifetime.CanDismiss, eventDataCodec, htmlString, variables);
 * </pre>
 *
 * @param <T> The event data type.
 */
public class HTMLCustomUIPage<T> extends InteractiveCustomUIPage<T> {

    /**
     * The player reference.
     */
    @Nonnull
    protected final PlayerRef playerRef;

    /**
     * The page lifetime.
     * Defaults to CustomPageLifetime.CanDismiss if not specified.
     */
    @Nonnull
    protected final CustomPageLifetime lifetime;

    /**
     * The event data codec.
     */
    @Nonnull
    protected final BuilderCodec<T> eventDataCodec;

    /**
     * The parsed HTML template (cached after first parse).
     */
    @Nullable
    private HTMLCustomUITemplate cachedTemplate;

    /**
     * Variables to override template variables.
     */
    @Nullable
    private Map<String, String> overrideVariables;

    /**
     * Output path for the HTML template file (user-customizable location).
     * This is the full path to the output file (including filename).
     * Once a file exists at this path, it will always be used instead of the input file.
     */
    @Nullable
    private Path outputPath;

    /**
     * Input path (directory) for the default HTML template file (inside mod assets).
     * This file will be copied to outputPath on first load if outputPath doesn't exist.
     * After the first copy, the input file is never used again.
     */
    @Nullable
    private Path inputPath;

    /**
     * Output path for saving the generated UI string (useful for debugging).
     * If set, the UI content will be saved to this file when the page is built.
     */
    @Nullable
    private Path uiOutputPath;

    /**
     * Create a new HTMLCustomUIPage from a HTML file.
     * 
     * @param playerRef The player reference.
     * @param eventDataCodec The event data codec.
     * @param htmlPath The path to the HTML file.
     */
    public HTMLCustomUIPage(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull Path htmlPath) {
        this(playerRef, CustomPageLifetime.CanDismiss, eventDataCodec);

        try {
            // Read HTML content from file
            String html = Files.readString(htmlPath);
            
            // Parse HTML and cache the template
            this.cachedTemplate = InterfaceBuilder.parse(html);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read HTML file", e);
        }
    }

    /**
     * Dummy codec for constructors that don't require codec upfront.
     * This will be replaced when build() is called.
     */
    @SuppressWarnings("unchecked")
    private static final BuilderCodec<?> DUMMY_CODEC = BuilderCodec.<HTMLCustomUITemplate.DynamicEventData>builder(
        HTMLCustomUITemplate.DynamicEventData.class,
        HTMLCustomUITemplate.DynamicEventData::new
    ).build();

    /**
     * Create a new HTMLCustomUIPage with default lifetime (CanDismiss).
     * This constructor creates a dummy codec that will be replaced when build() is called.
     * 
     * @param playerRef The player reference.
     */
    @SuppressWarnings("unchecked")
    public HTMLCustomUIPage(@Nonnull PlayerRef playerRef) {
        this(playerRef, CustomPageLifetime.CanDismiss, (BuilderCodec<T>) DUMMY_CODEC);
    }

    /**
     * Create a new HTMLCustomUIPage with default lifetime (CanDismiss).
     * 
     * @param playerRef The player reference.
     * @param eventDataCodec The event data codec.
     */
    public HTMLCustomUIPage(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec) {
        this(playerRef, CustomPageLifetime.CanDismiss, eventDataCodec);
    }

    /**
     * Create a new HTMLCustomUIPage.
     * 
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     */
    public HTMLCustomUIPage(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec) {
        super(
            playerRef,
            lifetime != null ? lifetime : CustomPageLifetime.CanDismiss,
            eventDataCodec
        );

        this.playerRef = playerRef;
        this.lifetime = lifetime != null ? lifetime : CustomPageLifetime.CanDismiss;
        this.eventDataCodec = eventDataCodec;
    }

    /**
     * Create a new HTMLCustomUIPage with override variables and default lifetime (CanDismiss).
     * 
     * @param playerRef The player reference.
     * @param eventDataCodec The event data codec.
     * @param overrideVariables Variables to override template variables (can be null).
     */
    public HTMLCustomUIPage(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nullable Map<String, String> overrideVariables) {
        this(playerRef, CustomPageLifetime.CanDismiss, eventDataCodec, overrideVariables);
    }

    /**
     * Create a new HTMLCustomUIPage with override variables.
     * 
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     * @param overrideVariables Variables to override template variables (can be null).
     */
    public HTMLCustomUIPage(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec, @Nullable Map<String, String> overrideVariables) {
        super(playerRef, lifetime != null ? lifetime : CustomPageLifetime.CanDismiss, eventDataCodec);
        this.playerRef = playerRef;
        this.lifetime = lifetime != null ? lifetime : CustomPageLifetime.CanDismiss;
        this.eventDataCodec = eventDataCodec;
        this.overrideVariables = overrideVariables;
    }


    /**
     * Build a HTMLCustomUIPage instance from the static HTML field of the calling class.
     * This method uses reflection to find the static HTML field in the subclass that called it.
     * 
     * <p>
     * <b>Example usage:</b>
     * </p>
     * <pre>
     * public class MyPage extends HTMLCustomUIPage {
     *     protected static final String HTML = "&lt;div&gt;My Page&lt;/div&gt;";
     * }
     * 
     * MyPage page = MyPage.build(playerRef);
     * </pre>
     * 
     * @param playerRef The player reference.
     * @return A HTMLCustomUIPage instance.
     * @throws IllegalStateException If HTML field is not found or not set in the calling class.
     */
    @Nonnull
    public static <P extends HTMLCustomUIPage<HTMLCustomUITemplate.DynamicEventData>> P build(@Nonnull PlayerRef playerRef) {
        // Get the calling class (the subclass that called build())
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Class<?> callingClass = null;
        
        // Find the first class in the stack trace that extends HTMLCustomUIPage (skip current class and internal calls)
        // Start from index 2 to skip getStackTrace() and build() itself
        for (int i = 2; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            
            // Skip internal Java classes and HTMLCustomUIPage itself
            if (className.startsWith("java.") || 
                className.startsWith("sun.") || 
                className.startsWith("com.sun.") ||
                className.equals(HTMLCustomUIPage.class.getName())) {
                continue;
            }
            
            try {
                Class<?> clazz = Class.forName(className);
                // Check if this class extends HTMLCustomUIPage but is not HTMLCustomUIPage itself
                if (HTMLCustomUIPage.class.isAssignableFrom(clazz) && clazz != HTMLCustomUIPage.class) {
                    callingClass = clazz;
                    break;
                }
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                // Continue searching - class might not be loaded yet
                continue;
            }
        }
        
        if (callingClass == null) {
            throw new IllegalStateException("Could not determine calling class. build() must be called from a subclass of HTMLCustomUIPage. " +
                "Make sure you're calling CustomHTMLPage.build() directly (not through a static import).");
        }
        
        // Create a temporary instance with a dummy codec to configure paths
        // Then parse HTML from paths and build the real codec
        BuilderCodec<HTMLCustomUITemplate.DynamicEventData> dummyCodec = BuilderCodec.<HTMLCustomUITemplate.DynamicEventData>builder(
            HTMLCustomUITemplate.DynamicEventData.class,
            HTMLCustomUITemplate.DynamicEventData::new
        ).build();
        
        // Create temporary instance to get paths configured in constructor
        P tempInstance;
        try {
            // Try to get constructor with only PlayerRef parameter
            java.lang.reflect.Constructor<?> constructor = callingClass.getDeclaredConstructor(PlayerRef.class);
            
            // Make constructor accessible (might be protected)
            constructor.setAccessible(true);
            
            // Create instance with only PlayerRef
            @SuppressWarnings("unchecked")
            P instance = (P) constructor.newInstance(playerRef);
            tempInstance = instance;
        } catch (NoSuchMethodException e) {
            // Fallback: try constructor with BuilderCodec (old way)
            try {
                // Try to get constructor with PlayerRef and BuilderCodec parameters
                java.lang.reflect.Constructor<?> constructor = callingClass.getDeclaredConstructor(PlayerRef.class, BuilderCodec.class);
                
                // Make constructor accessible (might be protected)
                constructor.setAccessible(true);
                
                // Create instance with PlayerRef and dummy codec
                @SuppressWarnings("unchecked")
                P instance = (P) constructor.newInstance(playerRef, dummyCodec);
                tempInstance = instance;
            } catch (NoSuchMethodException e2) {
                throw new IllegalStateException("Could not create instance of " + callingClass.getSimpleName() + 
                    ". The class must have a protected constructor that takes only PlayerRef.", e2);
            } catch (java.lang.reflect.InvocationTargetException | InstantiationException | IllegalAccessException e2) {
                throw new IllegalStateException("Failed to create instance of " + callingClass.getSimpleName() + ": " + e2.getMessage(), e2);
            }
        } catch (java.lang.reflect.InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to create instance of " + callingClass.getSimpleName() + ": " + e.getMessage(), e);
        }
        
        // Parse HTML from configured paths (if using withOutputPath/withInputPath)
        HTMLCustomUITemplate template;
        try {
            // Try to parse HTML from configured paths
            template = tempInstance.parseHTML();
        } catch (IllegalStateException e) {
            // Paths not configured - try static HTML field instead
            String htmlValue = null;
            try {
                // Get static HTML field from calling class
                java.lang.reflect.Field htmlField = callingClass.getDeclaredField("HTML");
                
                // Make field accessible (might be protected)
                htmlField.setAccessible(true);
                
                // Get HTML value from static field
                Object html = htmlField.get(null);
                if (html instanceof String) {
                    htmlValue = (String) html;
                }
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new IllegalStateException("HTML template not configured. Set withOutputPath()/withInputPath() in constructor or define a static HTML field.", e);
            }
            
            if (htmlValue == null || htmlValue.isEmpty()) {
                throw new IllegalStateException("HTML template not configured. Set withOutputPath()/withInputPath() in constructor or define a static HTML field.", e);
            }
            
            // Parse HTML from static field value
            template = InterfaceBuilder.parse(htmlValue);
        }
        
        // Build dynamic codec from template variables
        BuilderCodec<HTMLCustomUITemplate.DynamicEventData> codec = template.buildEventDataCodec();
        
        // If tempInstance was created with dummy codec, replace it via reflection
        // Otherwise, return the instance we already created
        if (tempInstance.eventDataCodec == dummyCodec) {
            try {
                // Get eventDataCodec field from HTMLCustomUIPage class
                java.lang.reflect.Field codecField = HTMLCustomUIPage.class.getDeclaredField("eventDataCodec");
                
                // Make field accessible (might be private)
                codecField.setAccessible(true);
                
                // Replace dummy codec with real codec
                codecField.set(tempInstance, codec);
                
                // Also update in superclass (InteractiveCustomUIPage)
                try {
                    // Get eventDataCodec field from superclass
                    java.lang.reflect.Field superCodecField = InteractiveCustomUIPage.class.getDeclaredField("eventDataCodec");
                    
                    // Make field accessible (might be private)
                    superCodecField.setAccessible(true);
                    
                    // Replace dummy codec with real codec in superclass
                    superCodecField.set(tempInstance, codec);
                } catch (Exception ex) {
                    // Superclass field might not exist or have different name
                }
            } catch (Exception ex) {
                // If we can't replace the codec, create a new instance
                try {
                    // Create new instance with real codec
                    java.lang.reflect.Constructor<?> constructor = callingClass.getDeclaredConstructor(PlayerRef.class, BuilderCodec.class);
                    constructor.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    P newInstance = (P) constructor.newInstance(playerRef, codec);
                    
                    // Copy paths and template from temp instance
                    try {
                        // Get field references
                        java.lang.reflect.Field outputPathField = HTMLCustomUIPage.class.getDeclaredField("outputPath");
                        java.lang.reflect.Field inputPathField = HTMLCustomUIPage.class.getDeclaredField("inputPath");
                        java.lang.reflect.Field cachedTemplateField = HTMLCustomUIPage.class.getDeclaredField("cachedTemplate");
                        
                        // Make fields accessible
                        outputPathField.setAccessible(true);
                        inputPathField.setAccessible(true);
                        cachedTemplateField.setAccessible(true);
                        
                        // Copy values from temp instance to new instance
                        outputPathField.set(newInstance, outputPathField.get(tempInstance));
                        inputPathField.set(newInstance, inputPathField.get(tempInstance));
                        cachedTemplateField.set(newInstance, cachedTemplateField.get(tempInstance));
                    } catch (Exception ex2) {
                        // If we can't copy fields, just return new instance
                    }
                    
                    return newInstance;
                } catch (Exception ex2) {
                    throw new IllegalStateException("Failed to create instance with codec: " + ex2.getMessage(), ex2);
                }
            }
        }
        
        return tempInstance;
    }

    /**
     * Build the Custom UI string from HTML string.
     * 
     * @param html The HTML content string.
     * @return The Custom UI string.
     */
    @Nonnull
    public static String build(@Nonnull String html) {
        HTMLCustomUITemplate template = InterfaceBuilder.parse(html);
        return template.build(new ComponentBuilderSettings(false));
    }

    /**
     * Build the Custom UI string from HTML with variables.
     * 
     * @param html The HTML content string.
     * @param variables Variables to override template variables (can be null).
     * @return The Custom UI string.
     */
    @Nonnull
    public static String build(@Nonnull String html, @Nullable Map<String, String> variables) {
        HTMLCustomUITemplate template = variables != null && !variables.isEmpty()
            ? InterfaceBuilder.parse(html, variables)
            : InterfaceBuilder.parse(html);
        return template.build(new ComponentBuilderSettings(false));
    }

    /**
     * Build the Custom UI string from this instance's HTML.
     * This is an instance method that uses getHTML().
     * 
     * @return The Custom UI string.
     */
    @Nonnull
    public String buildInstance() {
        HTMLCustomUITemplate template = this.parseHTML();
        return template.build(new ComponentBuilderSettings(false));
    }

    /**
     * Set the output path for the HTML template file (user-customizable location).
     * Once a file exists at this path, it will always be used instead of the input file.
     * 
     * @param outputPath The full output file path (including filename) where the user can customize the template.
     * @return This instance for chaining.
     */
    @Nonnull
    public HTMLCustomUIPage<T> withOutputPath(@Nullable Path outputPath) {
        this.outputPath = outputPath;
        // Clear cached template so it will be reloaded
        this.cachedTemplate = null;
        return this;
    }

    /**
     * Set the input path for the default HTML template file (inside mod assets).
     * This file will be copied to outputPath on first load if outputPath doesn't exist.
     * After the first copy, the input file is never used again.
     * 
     * @param inputPath The input path (inside mod assets) containing the default template.
     *                  Can be null, in which case only outputPath will be used.
     * @return This instance for chaining.
     */
    @Nonnull
    public HTMLCustomUIPage<T> withInputPath(@Nullable Path inputPath) {
        this.inputPath = inputPath;
        // Clear cached template so it will be reloaded
        this.cachedTemplate = null;
        return this;
    }

    /**
     * Set the output path for saving the generated UI string (useful for debugging).
     * If set, the UI content will be saved to this file when the page is built.
     * 
     * @param uiOutputPath The full path (including filename) where the UI string should be saved.
     * @return This instance for chaining.
     */
    @Nonnull
    public HTMLCustomUIPage<T> withUIOutputPath(@Nonnull Path uiOutputPath) {
        this.uiOutputPath = uiOutputPath;
        return this;
    }

    /**
     * Parse the HTML content and build the UI.
     * This method is called during the build process and caches the result.
     * Uses HTMLCustomUITemplate.loadFromFile() if paths are set, otherwise throws an exception.
     * 
     * @return The parsed Custom UI template.
     * @throws IllegalStateException If paths are not set and HTML cannot be loaded.
     */
    @Nonnull
    protected HTMLCustomUITemplate parseHTML() {
        if (this.cachedTemplate != null) {
            return this.cachedTemplate;
        }

        String html;
        
        // Use HTMLCustomUITemplate.loadFromFile() if paths are set
        // This method handles: if output exists, use it; if not, copy from input to output and use output
        if (this.outputPath != null || this.inputPath != null) {
            try {
                // Extract filename and directory from outputPath (outputPath is now a full file path)
                String filename;
                Path outputDir;
                
                // If outputPath is set, use it
                if (this.outputPath != null) {
                    filename = this.outputPath.getFileName().toString();
                    outputDir = this.outputPath.getParent();
                } else
                // If inputPath is set, use it
                if (this.inputPath != null && java.nio.file.Files.isRegularFile(this.inputPath)) {
                    // Fallback: use inputPath as file and extract filename from it
                    filename = this.inputPath.getFileName().toString();
                    outputDir = null;
                } else {
                    throw new IllegalStateException("HTML template not configured. Set withOutputPath() with a full file path.");
                }
                
                html = HTMLCustomUITemplate.loadFromFile(filename, outputDir, this.inputPath);
            } catch (java.io.FileNotFoundException e) {
                String filename = this.outputPath != null ? this.outputPath.getFileName().toString() : "unknown";
                throw new IllegalStateException("HTML template file not found: " + filename + 
                    " (searched in output: " + this.outputPath + ", input: " + this.inputPath + "). " +
                    "Make sure the input file exists and is accessible.", e);
            } catch (java.io.IOException e) {
                String filename = this.outputPath != null ? this.outputPath.getFileName().toString() : "unknown";
                throw new RuntimeException("Failed to load or copy HTML template file: " + filename + 
                    ". Error: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalStateException("HTML template not configured. Call withOutputPath() and/or withInputPath() before using this page.");
        }
        
        // Parse HTML with override variables if provided
        HTMLCustomUITemplate template = this.overrideVariables != null && !this.overrideVariables.isEmpty()
            ? InterfaceBuilder.parse(html, this.overrideVariables)
            : InterfaceBuilder.parse(html);

        // Cache the result
        this.cachedTemplate = template;

        return template;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        // Parse HTML and get the UI string
        HTMLCustomUITemplate template = this.parseHTML();
        String uiString = template.build(new ComponentBuilderSettings(false));

        // Limit UI string to 4MB to prevent server overload
        final long MAX_UI_SIZE = 4L * 1024 * 1024; // 4MB in bytes
        long uiSizeBytes = uiString.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        
        if (uiSizeBytes > MAX_UI_SIZE) {
            throw new IllegalStateException(String.format(
                "Generated UI string is too large: %d bytes (max: %d bytes / 4MB). " +
                "The UI template is generating too much content. Please simplify your HTML template.",
                uiSizeBytes, MAX_UI_SIZE
            ));
        }
        
        // Save UI string to file if uiOutputPath is set (useful for debugging)
        if (this.uiOutputPath != null) {
            try {
                Path parentDir = this.uiOutputPath.getParent();

                // Create parent directory if it doesn't exist
                if (parentDir != null) {
                    Files.createDirectories(parentDir);
                }

                // Delete file first to ensure clean write (no appending)
                if (Files.exists(this.uiOutputPath)) {
                    Files.delete(this.uiOutputPath);
                }

                Files.writeString(this.uiOutputPath, uiString, java.nio.charset.StandardCharsets.UTF_8);
            } catch (IOException e) {
                // Log error but don't fail the build
                System.err.println("Failed to save UI output to file: " + this.uiOutputPath + ". Error: " + e.getMessage());
            }
        }

        // Create empty Group #MIBRoot first (required for HTMLCustomUIPage to display)
        commandBuilder.append("Pages/MInterfaceBuilder_Dummy.ui");
        
        // Append the UI inline to the #MIBRoot group
        commandBuilder.appendInline("#MIBRoot", uiString);

        // Allow subclasses to add custom bindings or modifications
        this.buildCustom(ref, commandBuilder, eventBuilder, store);
    }

    /**
     * Override this method to add custom event bindings or UI modifications after the HTML is parsed and added.
     * The default implementation does nothing.
     * 
     * @param ref The entity store reference.
     * @param commandBuilder The UI command builder.
     * @param eventBuilder The UI event builder.
     * @param store The entity store.
     */
    protected void buildCustom(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        // Default: do nothing, subclasses can override
    }

    /**
     * Get the parsed template (useful for accessing variables or components).
     * 
     * @return The parsed Custom UI template, or null if not yet parsed.
     */
    @Nullable
    public HTMLCustomUITemplate getParsedTemplate() {
        return this.cachedTemplate;
    }

    /**
     * Build a dynamic BuilderCodec based on the variables in the parsed template.
     * This method parses the HTML first if not already parsed, then builds a codec
     * from the template's variables.
     * 
     * @return A BuilderCodec for DynamicEventData that can handle all variables in the template.
     */
    @Nonnull
    public BuilderCodec<HTMLCustomUITemplate.DynamicEventData> buildEventDataCodec() {
        HTMLCustomUITemplate template = this.parseHTML();
        return template.buildEventDataCodec();
    }

    /**
     * Create a new HTMLCustomUIPage from HTML string with default lifetime (CanDismiss).
     * 
     * @param <T> The event data type.
     * @param eventDataCodec The event data codec.
     * @param html The HTML content string.
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromHTML(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String html) {
        return fromHTML(playerRef, null, eventDataCodec, html);
    }

    /**
     * Create a new HTMLCustomUIPage from HTML string.
     * 
     * @param <T> The event data type.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     * @param html The HTML content string.
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromHTML(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String html) {
        HTMLCustomUIPage<T> page = new HTMLCustomUIPage<T>(playerRef, lifetime, eventDataCodec);
        // Parse HTML immediately and cache it
        page.cachedTemplate = InterfaceBuilder.parse(html);
        return page;
    }

    /**
     * Create a new HTMLCustomUIPage from HTML string with variables and default lifetime (CanDismiss).
     * 
     * @param <T> The event data type.
     * @param eventDataCodec The event data codec.
     * @param html The HTML content string.
     * @param variables Variables to override template variables (can be null).
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromHTML(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String html, @Nullable Map<String, String> variables) {
        return fromHTML(playerRef, null, eventDataCodec, html, variables);
    }

    /**
     * Create a new HTMLCustomUIPage from HTML string with variables.
     * 
     * @param <T> The event data type.
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     * @param html The HTML content string.
     * @param variables Variables to override template variables (can be null).
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromHTML(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String html, @Nullable Map<String, String> variables) {
        HTMLCustomUIPage<T> page = new HTMLCustomUIPage<T>(playerRef, lifetime, eventDataCodec, variables);
        // Parse HTML immediately and cache it
        page.cachedTemplate = variables != null && !variables.isEmpty()
            ? InterfaceBuilder.parse(html, variables)
            : InterfaceBuilder.parse(html);
        return page;
    }

    /**
     * Create a new HTMLCustomUIPage from a file path with default lifetime (CanDismiss).
     * 
     * @param <T> The event data type.
     * @param eventDataCodec The event data codec.
     * @param htmlPath The path to the HTML file.
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromFile(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull Path htmlPath) {
        return fromFile(playerRef, null, eventDataCodec, htmlPath);
    }

    /**
     * Create a new HTMLCustomUIPage from a file path.
     * 
     * @param <T> The event data type.
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     * @param htmlPath The path to the HTML file.
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromFile(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull Path htmlPath) {
        HTMLCustomUIPage<T> page = new HTMLCustomUIPage<T>(playerRef, lifetime, eventDataCodec);
        try {
            // Read HTML content from file
            String html = Files.readString(htmlPath);
            
            // Parse HTML immediately and cache it
            page.cachedTemplate = InterfaceBuilder.parse(html);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read HTML file: " + htmlPath, e);
        }
        return page;
    }

    /**
     * Create a new HTMLCustomUIPage from a file path with variables and default lifetime (CanDismiss).
     * 
     * @param <T> The event data type.
     * @param eventDataCodec The event data codec.
     * @param htmlPath The path to the HTML file.
     * @param variables Variables to override template variables (can be null).
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromFile(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull Path htmlPath, @Nullable Map<String, String> variables) {
        return fromFile(playerRef, null, eventDataCodec, htmlPath, variables);
    }

    /**
     * Create a new HTMLCustomUIPage from a file path with variables.
     * 
     * @param <T> The event data type.
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     * @param htmlPath The path to the HTML file.
     * @param variables Variables to override template variables (can be null).
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromFile(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull Path htmlPath, @Nullable Map<String, String> variables) {
        HTMLCustomUIPage<T> page = new HTMLCustomUIPage<T>(playerRef, lifetime, eventDataCodec, variables);
        try {
            // Read HTML content from file
            String html = Files.readString(htmlPath);
            
            // Parse HTML immediately and cache it (with variables if provided)
            page.cachedTemplate = variables != null && !variables.isEmpty()
                ? InterfaceBuilder.parse(html, variables)
                : InterfaceBuilder.parse(html);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read HTML file: " + htmlPath, e);
        }
        return page;
    }

    /**
     * Create a new HTMLCustomUIPage from an asset path (Hytale server asset) with default lifetime (CanDismiss).
     * 
     * @param <T> The event data type.
     * @param eventDataCodec The event data codec.
     * @param assetPath The asset path (e.g., "ui/pages/login.html").
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromAsset(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String assetPath) {
        return fromAsset(playerRef, null, eventDataCodec, assetPath);
    }

    /**
     * Create a new HTMLCustomUIPage from an asset path (Hytale server asset).
     * 
     * @param <T> The event data type.
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     * @param assetPath The asset path (e.g., "ui/pages/login.html").
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromAsset(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String assetPath) {
        HTMLCustomUIPage<T> page = new HTMLCustomUIPage<T>(playerRef, lifetime, eventDataCodec);
        try {
            // Parse asset immediately and cache it
            page.cachedTemplate = InterfaceBuilder.parseAsset(assetPath);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to parse asset: " + assetPath, e);
        }
        return page;
    }

    /**
     * Create a new HTMLCustomUIPage from an asset path with variables and default lifetime (CanDismiss).
     * 
     * @param <T> The event data type.
     * @param eventDataCodec The event data codec.
     * @param assetPath The asset path (e.g., "ui/pages/login.html").
     * @param variables Variables to override template variables (can be null).
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromAsset(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String assetPath, @Nullable Map<String, String> variables) {
        return fromAsset(playerRef, null, eventDataCodec, assetPath, variables);
    }

    /**
     * Create a new HTMLCustomUIPage from an asset path with variables.
     * 
     * @param <T> The event data type.
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     * @param assetPath The asset path (e.g., "ui/pages/login.html").
     * @param variables Variables to override template variables (can be null).
     * @return A new HTMLCustomUIPage instance.
     */
    @Nonnull
    public static <T> HTMLCustomUIPage<T> fromAsset(@Nonnull PlayerRef playerRef, @Nullable CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> eventDataCodec, @Nonnull String assetPath, @Nullable Map<String, String> variables) {
        HTMLCustomUIPage<T> page = new HTMLCustomUIPage<T>(playerRef, lifetime, eventDataCodec, variables);
        try {
            // Parse asset immediately and cache it (with variables if provided)
            page.cachedTemplate = variables != null && !variables.isEmpty()
                ? InterfaceBuilder.parseAsset(assetPath, variables)
                : InterfaceBuilder.parseAsset(assetPath);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to parse asset: " + assetPath, e);
        }
        return page;
    }
}
