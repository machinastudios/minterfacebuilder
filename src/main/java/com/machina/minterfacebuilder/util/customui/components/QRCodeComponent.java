package com.machina.minterfacebuilder.util.customui.components;

import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

/**
 * Custom component for rendering QR codes in Custom UI.
 * Requires the 'data' attribute to specify the QR code content.
 */
public class QRCodeComponent extends ComponentBuilder {
    /**
     * Default block size for QR code squares.
     */
    private static final int DEFAULT_BLOCK_SIZE = 6;

    /**
     * Create a QRCodeComponent from HTML attributes.
     * @param attributes The HTML attributes map.
     */
    public QRCodeComponent() {
        this(Map.of());
    }

    public QRCodeComponent(Map<String, String> attributes) {
        super("Group");

        Map<String, String> safeAttributes = attributes != null ? attributes : Map.of();

        String data = safeAttributes.get("data");
        if (data == null || data.isEmpty()) {
            data = safeAttributes.get("value");
        }
        if (data == null || data.isEmpty()) {
            // No data provided, return empty group
            return;
        }

        // Get block size from attributes
        int blockSize = DEFAULT_BLOCK_SIZE;
        String blockSizeStr = safeAttributes.get("blocksize");
        if (blockSizeStr == null || blockSizeStr.isEmpty()) {
            blockSizeStr = safeAttributes.get("block-size");
        }
        if (blockSizeStr != null && !blockSizeStr.isEmpty()) {
            try {
                blockSize = Integer.parseInt(blockSizeStr);
            } catch (NumberFormatException e) {
                // Use default
            }
        }

        // Copy other properties
        String id = safeAttributes.get("id");
        if (id != null && !id.isEmpty()) {
            this.setId(id);
        }

        // Generate QR code
        this.generateQRCode(data, blockSize, safeAttributes);
    }

    /**
     * Generate QR code squares.
     * @param data The data to encode.
     * @param blockSize The size of each block.
     * @param attributes Additional attributes.
     */
    private void generateQRCode(String data, int blockSize, Map<String, String> attributes) {
        // Build properties map from attributes
        Map<String, Object> props = new java.util.HashMap<>();
        props.put("BlockSize", blockSize);
        
        // Copy other properties from attributes
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (!key.equals("data") && !key.equals("value") && !key.equals("blocksize") && !key.equals("block-size") && !key.equals("id")) {
                try {
                    // Try to parse as number
                    props.put(key, Integer.parseInt(entry.getValue()));
                } catch (NumberFormatException e) {
                    // Use as string
                    props.put(key, entry.getValue());
                }
            }
        }

        // Apply properties to this component
        if (!props.isEmpty()) {
            this.setProperties(props);
        }

        // Get the matrix
        BitMatrix matrix = generateMatrix(data);

        // If the matrix is null, create a placeholder comment
        if (matrix == null) {
            this.addComment("QR Code component - data: " + data + " (generation failed)");
            return;
        }

        // Iterate over the matrix rows
        for (int y = 0; y < matrix.getHeight(); y++) {
            // Iterate over the matrix columns
            for (int x = 0; x < matrix.getWidth(); x++) {
                String color = matrix.get(x, y) ? "black" : "white";

                // If the color is white, skip the square
                if (color.equals("white")) {
                    continue;
                }

                SquareCustomUIComponent square = new SquareCustomUIComponent(color, blockSize, blockSize);

                // Get the anchor property
                var anchor = square.<Map<String, Object>>getProperty("Anchor");

                // Set the X and Y anchor position
                anchor.put("Top", y * blockSize);
                anchor.put("Left", x * blockSize);

                // Append the square to this component
                this.appendChild(square);
            }

            // Add one white square at the end of each line
            SquareCustomUIComponent whiteSquare = new SquareCustomUIComponent("white", blockSize, blockSize);
            var anchor = whiteSquare.<Map<String, Object>>getProperty("Anchor");
            anchor.put("Top", y * blockSize);
            anchor.put("Left", (matrix.getWidth() - 1) * blockSize);
            this.appendChild(whiteSquare);
        }
    }

    /**
     * Generate a custom UI QR code from the given data.
     * @param data The data to encode.
     * @return The custom UI QR code component builder.
     */
    public static ComponentBuilder withData(String data) {
        return generateCustomUIQRCode(data, Map.of());
    }

    /**
     * Generate a custom UI QR code from the given data.
     * @param data The data to encode.
     * @param properties The properties to use for the QR code.
     * @return The custom UI QR code component builder.
     */
    public static ComponentBuilder generateCustomUIQRCode(String data, Map<String, Object> properties) {
        // Get the BlockSize property from the properties map
        int blockSize = properties.get("BlockSize") != null ? (int) properties.get("BlockSize") : DEFAULT_BLOCK_SIZE;

        // Create a new group for the QR code
        ComponentBuilder group = ComponentBuilder.create("Group")
            .setProperties(properties);

        // Get the matrix
        BitMatrix matrix = generateMatrix(data);

        // If the matrix is null, return null
        if (matrix == null) {
            return null;
        }

        // Iterate over the matrix rows
        for (int y = 0; y < matrix.getHeight(); y++) {
            // Iterate over the matrix columns
            for (int x = 0; x < matrix.getWidth(); x++) {
                String color = matrix.get(x, y) ? "black" : "white";

                // If the color is white, skip the square
                if (color.equals("white")) {
                    continue;
                }

                SquareCustomUIComponent square = new SquareCustomUIComponent(color, blockSize, blockSize);

                // Get the anchor property
                var anchor = square.<Map<String, Object>>getProperty("Anchor");

                // Set the X and Y anchor position
                anchor.put("Top", y * blockSize);
                anchor.put("Left", x * blockSize);

                // Append the square to the group
                group.appendChild(square);
            }

            // Add one white square at the end of each line
            SquareCustomUIComponent whiteSquare = new SquareCustomUIComponent("white", blockSize, blockSize);
            var anchor = whiteSquare.<Map<String, Object>>getProperty("Anchor");
            anchor.put("Top", y * blockSize);
            anchor.put("Left", (matrix.getWidth() - 1) * blockSize);
            group.appendChild(whiteSquare);
        }

        return group;
    }

    /**
     * Generate a matrix from the given data.
     * @param data The data to encode.
     * @return The matrix.
     */
    private static BitMatrix generateMatrix(String data) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix;

        try {
            // Encode the data into a QR code
            matrix = writer.encode(
                data,
                BarcodeFormat.QR_CODE,
                40,
                40,
                Map.of(
                    EncodeHintType.MARGIN, 1
                )
            );
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

        return matrix;
    }
}
