package com.machina.minterfacebuilder.util.customui.components;

import java.util.Map;

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
    public QRCodeComponent(Map<String, String> attributes) {
        super("Group");

        String data = attributes.get("data");
        if (data == null || data.isEmpty()) {
            data = attributes.get("value");
        }
        if (data == null || data.isEmpty()) {
            // No data provided, return empty group
            return;
        }

        // Get block size from attributes
        int blockSize = DEFAULT_BLOCK_SIZE;
        String blockSizeStr = attributes.get("blocksize");
        if (blockSizeStr == null || blockSizeStr.isEmpty()) {
            blockSizeStr = attributes.get("block-size");
        }
        if (blockSizeStr != null && !blockSizeStr.isEmpty()) {
            try {
                blockSize = Integer.parseInt(blockSizeStr);
            } catch (NumberFormatException e) {
                // Use default
            }
        }

        // Copy other properties
        String id = attributes.get("id");
        if (id != null && !id.isEmpty()) {
            this.setId(id);
        }

        // Generate QR code
        this.generateQRCode(data, blockSize, attributes);
    }

    /**
     * Generate QR code squares.
     * Tries to use QRCodeUtil from mauth if available, otherwise creates a placeholder.
     * @param data The data to encode.
     * @param blockSize The size of each block.
     * @param attributes Additional attributes.
     */
    private void generateQRCode(String data, int blockSize, Map<String, String> attributes) {
        // Try to use QRCodeUtil from mauth if available
        try {
            Class<?> qrCodeUtilClass = Class.forName("com.machina.mauth.utils.QRCodeUtil");
            java.lang.reflect.Method generateMethod = qrCodeUtilClass.getMethod(
                "generateCustomUIQRCode", 
                String.class, 
                Map.class
            );

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

            ComponentBuilder qrCodeBuilder = (ComponentBuilder) generateMethod.invoke(null, data, props);
            
            // Use the generated QR code builder
            if (qrCodeBuilder != null) {
                // The QR code builder is already a Group with all the squares as children
                // We need to copy its structure to this component
                // Since we can't access private fields, we'll build it and append the result
                // as a string reference, or we can use reflection to copy
                
                // Simple approach: append the QR code builder as a child
                // This will include all its children in the final output
                this.appendChild(qrCodeBuilder);
                
                return;
            }
        } catch (Exception e) {
            // QRCodeUtil not available, use placeholder implementation
        }

        // Fallback: Create a placeholder
        this.addComment("QR Code component - data: " + data + " (QRCodeUtil not available)");
        
        // Apply properties from attributes
        Map<String, Object> props = new java.util.HashMap<>();
        props.put("BlockSize", blockSize);
        
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (!key.equals("data") && !key.equals("value") && !key.equals("blocksize") && !key.equals("block-size") && !key.equals("id")) {
                try {
                    // Try to parse as number
                    props.put(key, Integer.parseInt(entry.getValue()));
                } catch (NumberFormatException ex) {
                    // Use as string
                    props.put(key, entry.getValue());
                }
            }
        }
        if (!props.isEmpty()) {
            this.setProperties(props);
        }
    }
}
