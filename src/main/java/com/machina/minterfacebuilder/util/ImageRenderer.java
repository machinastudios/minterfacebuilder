package com.machina.minterfacebuilder.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.ComponentBuilderSettings;

/**
 * Utility class for rendering images pixel by pixel in Custom UI.
 * <p>
 * This class converts images (from files or URLs) into Custom UI components by rendering each pixel
 * as a colored square. Images can be downscaled automatically if they exceed maximum dimensions,
 * and white pixels can be skipped for transparency effects.
 * </p>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>
 * // Render from file path
 * ComponentBuilder image = ImageRenderer.renderImage("path/to/image.png");
 *
 * // Render from URL with custom properties
 * Map&lt;String, Object&gt; props = new HashMap&lt;&gt;();
 * props.put("BlockSize", 2);
 * props.put("MaxWidth", 100);
 * props.put("MaxHeight", 100);
 * props.put("SkipWhite", true);
 * ComponentBuilder image = ImageRenderer.renderImage("https://example.com/image.png", props);
 * </pre>
 * <p>
 * <b>Supported properties:</b>
 * </p>
 * <ul>
 *   <li><b>BlockSize</b> (Integer): Size of each pixel square in Custom UI units (default: 1)</li>
 *   <li><b>MaxWidth</b> (Integer): Maximum width before downscaling (default: 96)</li>
 *   <li><b>MaxHeight</b> (Integer): Maximum height before downscaling (default: 96)</li>
 *   <li><b>SkipWhite</b> (Boolean): Whether to skip white pixels (default: false)</li>
 * </ul>
 * <p>
 * <b>Note:</b> Large images will be automatically downscaled by skipping pixels when they exceed
 * the maximum width or height. The downscaling algorithm maintains the aspect ratio by calculating
 * skip factors.
 * </p>
 */
public class ImageRenderer {
    /**
     * The default block size (size of each pixel square in Custom UI units).
     */
    private static final int DEFAULT_BLOCK_SIZE = 1;

    /**
     * The default max width for rendering (images larger will be downscaled).
     * Images exceeding this width will have pixels skipped to fit within the limit.
     */
    private static final int DEFAULT_MAX_WIDTH = 96;

    /**
     * The default max height for rendering (images larger will be downscaled).
     * Images exceeding this height will have pixels skipped to fit within the limit.
     */
    private static final int DEFAULT_MAX_HEIGHT = 96;

    /**
     * Render an image pixel by pixel into a Custom UI ComponentBuilder using default properties.
     * <p>
     * The image path can be either a local file path or a URL (http:// or https://).
     * If the image cannot be loaded, this method returns null.
     * </p>
     * <p>
     * <b>Example:</b>
     * </p>
     * <pre>
     * ComponentBuilder image = ImageRenderer.renderImage("assets/logo.png");
     * // or
     * ComponentBuilder image = ImageRenderer.renderImage("https://example.com/image.png");
     * </pre>
     *
     * @param imagePath The path to the image file or URL (as String).
     *                  Can be a local file path or HTTP/HTTPS URL.
     * @return The ComponentBuilder containing the rendered image as a Group with pixel squares,
     *         or null if the image could not be loaded.
     */
    public static ComponentBuilder renderImage(String imagePath) {
        return renderImage(imagePath, Map.of());
    }

    /**
     * Render an image pixel by pixel into a Custom UI ComponentBuilder with custom properties.
     * <p>
     * The image path can be either a local file path or a URL (http:// or https://).
     * If the image cannot be loaded, this method returns null.
     * </p>
     * <p>
     * <b>Properties:</b>
     * </p>
     * <ul>
     *   <li><b>BlockSize</b> (Integer): Size of each pixel square in Custom UI units (default: 1).
     *       Larger values create bigger pixel squares, making the image more visible but less detailed.</li>
     *   <li><b>MaxWidth</b> (Integer): Maximum width in pixels before downscaling (default: 96).
     *       Images wider than this will have pixels skipped horizontally.</li>
     *   <li><b>MaxHeight</b> (Integer): Maximum height in pixels before downscaling (default: 96).
     *       Images taller than this will have pixels skipped vertically.</li>
     *   <li><b>SkipWhite</b> (Boolean): Whether to skip white pixels (default: false).
     *       When true, pixels with RGB values all above 240 are not rendered, creating transparency effects.</li>
     * </ul>
     * <p>
     * <b>Example:</b>
     * </p>
     * <pre>
     * Map&lt;String, Object&gt; props = new HashMap&lt;&gt;();
     * props.put("BlockSize", 2);        // Each pixel is 2x2 units
     * props.put("MaxWidth", 128);       // Downscale if wider than 128px
     * props.put("MaxHeight", 128);      // Downscale if taller than 128px
     * props.put("SkipWhite", true);     // Skip white pixels
     * ComponentBuilder image = ImageRenderer.renderImage("logo.png", props);
     * </pre>
     *
     * @param imagePathOrUrl The path to the image file or URL (as String).
     *                       Can be a local file path or HTTP/HTTPS URL.
     * @param properties The properties to use for the image rendering.
     *                   Can include BlockSize, MaxWidth, MaxHeight, and SkipWhite.
     *                   Invalid or missing properties will use default values.
     * @return The ComponentBuilder containing the rendered image as a Group with pixel squares,
     *         or null if the image could not be loaded or is null/empty.
     */
    public static ComponentBuilder renderImage(String imagePathOrUrl, Map<String, Object> properties) {
        if (imagePathOrUrl == null || imagePathOrUrl.isEmpty()) {
            return null;
        }

        BufferedImage image = null;

        // Check if it's a URL (http:// or https://)
        if (imagePathOrUrl.startsWith("http://") || imagePathOrUrl.startsWith("https://")) {
            image = loadImageFromUrl(imagePathOrUrl);
        } else {
            // Try to load from file path
            image = loadImageFromFile(imagePathOrUrl);
        }

        // If image failed to load, return null
        if (image == null) {
            return null;
        }

        return renderImage(image, properties);
    }

    /**
     * Load an image from a local file path.
     * <p>
     * Attempts to read an image file using Java's ImageIO.
     * </p>
     *
     * @param filePath The path to the image file.
     * @return The BufferedImage if successfully loaded, or null if the file cannot be read or does not exist.
     */
    private static BufferedImage loadImageFromFile(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Load an image from an HTTP or HTTPS URL.
     * <p>
     * Creates an HTTP connection to fetch the image from the URL.
     * Uses a 5-second timeout for both connection and reading.
     * </p>
     *
     * @param urlString The HTTP or HTTPS URL to the image.
     * @return The BufferedImage if successfully loaded, or null if the URL cannot be accessed,
     *         returns a non-OK status code, or any error occurs during loading.
     */
    private static BufferedImage loadImageFromUrl(String urlString) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000); // 5 seconds timeout
            connection.connect();

            // Check if response is OK
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            inputStream = connection.getInputStream();
            return ImageIO.read(inputStream);
        } catch (Exception e) {
            return null;
        } finally {
            // Close resources
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Render a BufferedImage pixel by pixel into a Custom UI ComponentBuilder using default properties.
     * <p>
     * This method is useful when you already have a BufferedImage object loaded in memory.
     * </p>
     * <p>
     * <b>Example:</b>
     * </p>
     * <pre>
     * BufferedImage img = ImageIO.read(new File("image.png"));
     * ComponentBuilder component = ImageRenderer.renderImage(img);
     * </pre>
     *
     * @param image The BufferedImage to render. Must not be null.
     * @return The ComponentBuilder containing the rendered image as a Group with pixel squares,
     *         or null if the image is null.
     */
    public static ComponentBuilder renderImage(BufferedImage image) {
        return renderImage(image, Map.of());
    }

    /**
     * Render a BufferedImage pixel by pixel into a Custom UI ComponentBuilder with custom properties.
     * <p>
     * This is the core rendering method that converts each pixel of the image into a colored square
     * in the Custom UI. The method handles downscaling by skipping pixels when the image exceeds
     * maximum dimensions, and can optionally skip white pixels for transparency effects.
     * </p>
     * <p>
     * <b>Rendering process:</b>
     * </p>
     * <ol>
     *   <li>Calculate downscaling factors if image exceeds MaxWidth or MaxHeight</li>
     *   <li>Create a Group component to hold all pixel squares</li>
     *   <li>Iterate through pixels (with skipping for downscaling)</li>
     *   <li>For each pixel: convert color to hex, create a square component, and add to group</li>
     *   <li>Skip fully transparent pixels (alpha = 0)</li>
     *   <li>Optionally skip white pixels if SkipWhite is enabled</li>
     * </ol>
     * <p>
     * <b>Properties:</b>
     * </p>
     * <ul>
     *   <li><b>BlockSize</b> (Integer): Size of each pixel square in Custom UI units (default: 1)</li>
     *   <li><b>MaxWidth</b> (Integer): Maximum width in pixels before downscaling (default: 96)</li>
     *   <li><b>MaxHeight</b> (Integer): Maximum height in pixels before downscaling (default: 96)</li>
     *   <li><b>SkipWhite</b> (Boolean): Whether to skip white pixels, RGB >= 240 (default: false)</li>
     * </ul>
     *
     * @param image The BufferedImage to render. Must not be null.
     * @param properties The properties to use for the image rendering.
     *                   Can include BlockSize, MaxWidth, MaxHeight, and SkipWhite.
     *                   Invalid or missing properties will use default values.
     * @return The ComponentBuilder containing the rendered image as a Group with pixel squares,
     *         or null if the image is null.
     */
    public static ComponentBuilder renderImage(BufferedImage image, Map<String, Object> properties) {
        if (image == null) {
            return null;
        }

        // Extract properties
        int blockSize = properties.get("BlockSize") != null ? (int) properties.get("BlockSize") : DEFAULT_BLOCK_SIZE;
        int maxWidth = properties.get("MaxWidth") != null ? (int) properties.get("MaxWidth") : DEFAULT_MAX_WIDTH;
        int maxHeight = properties.get("MaxHeight") != null ? (int) properties.get("MaxHeight") : DEFAULT_MAX_HEIGHT;
        boolean skipWhite = properties.get("SkipWhite") != null ? (boolean) properties.get("SkipWhite") : false;

        // Calculate downscaling if needed
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        
        int renderWidth = originalWidth;
        int renderHeight = originalHeight;
        int skipX = 1;
        int skipY = 1;

        // Calculate skip factors if downscaling is needed
        if (originalWidth > maxWidth) {
            skipX = (int) Math.ceil((double) originalWidth / maxWidth);
            renderWidth = originalWidth / skipX;
        }
        if (originalHeight > maxHeight) {
            skipY = (int) Math.ceil((double) originalHeight / maxHeight);
            renderHeight = originalHeight / skipY;
        }

        // Create a new group for the image
        ComponentBuilder group = ComponentBuilder.create("Group")
            .setProperties(properties)
            .setSettings(new ComponentBuilderSettings(true));

        // Iterate over the image pixels (with skipping for downscaling)
        for (int renderY = 0; renderY < renderHeight; renderY++) {
            int sourceY = renderY * skipY;
            
            for (int renderX = 0; renderX < renderWidth; renderX++) {
                int sourceX = renderX * skipX;
                
                // Get the pixel color at the source position
                int rgb = image.getRGB(sourceX, sourceY);
                Color pixelColor = new Color(rgb, true); // true = has alpha channel

                // Skip fully transparent pixels
                if (pixelColor.getAlpha() == 0) {
                    continue;
                }

                // Skip white pixels if requested
                if (skipWhite && isWhite(pixelColor)) {
                    continue;
                }

                // Convert color to hex string
                String colorHex = colorToHex(pixelColor);

                // Create a square component for this pixel
                ComponentBuilder square = ComponentBuilder.create("Group")
                    .setProperty("Background", colorHex)
                    .setProperty("Anchor", Map.of(
                        "Top", renderY * blockSize,
                        "Left", renderX * blockSize,
                        "Width", blockSize,
                        "Height", blockSize
                    ));

                // Append the square to the group
                group.appendChild(square);
            }
        }

        return group;
    }

    /**
     * Convert a Color to a hexadecimal string representation.
     * <p>
     * If the color is fully opaque (alpha = 255), returns RGB format (#RRGGBB).
     * If the color has transparency (alpha < 255), returns RGBA format (#RRGGBBAA).
     * </p>
     * <p>
     * <b>Examples:</b>
     * </p>
     * <ul>
     *   <li>Fully opaque red: "#FF0000"</li>
     *   <li>50% transparent blue: "#0000FF80"</li>
     * </ul>
     *
     * @param color The Color object to convert.
     * @return The hex string representation in format "#RRGGBB" or "#RRGGBBAA".
     */
    private static String colorToHex(Color color) {
        // If fully opaque, use RGB
        if (color.getAlpha() == 255) {
            return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        }
        
        // If has transparency, include alpha
        return String.format("#%02X%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Check if a color is white or very close to white.
     * <p>
     * A color is considered white if all RGB components are 240 or higher (out of 255).
     * This threshold is used to skip white pixels when the SkipWhite property is enabled,
     * which is useful for images with white backgrounds that should be transparent.
     * </p>
     *
     * @param color The Color object to check.
     * @return True if the color is white (all RGB components >= 240), false otherwise.
     */
    private static boolean isWhite(Color color) {
        // Consider a color white if all RGB components are above 240
        return color.getRed() >= 240 && color.getGreen() >= 240 && color.getBlue() >= 240;
    }
}
