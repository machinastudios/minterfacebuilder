package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H TextField component with inline expanded styles.
 * Based on @TextField from Common.ui (line 424-432).
 */
public class HTextField extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HTextField";

    public HTextField() {
        this(null);
    }

    public HTextField(Map<String, String> attributes) {
        super("TextField");
        
        // Configure Style
        Map<String, Object> style = new HashMap<>();
        this.setProperty("Style", style);
        
        // Configure PlaceholderStyle
        Map<String, Object> placeholderStyle = new HashMap<>();
        placeholderStyle.put("TextColor", "#6e7da1");
        this.setProperty("PlaceholderStyle", placeholderStyle);
        
        // Configure Background
        this.setProperty("Background", "PatchStyle(TexturePath: \"Common/InputBox.png\", Border: 16)");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", 38);
        this.setProperty("Anchor", anchor);
        
        // Configure Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 10);
        this.setProperty("Padding", padding);
        
        // Text value
        if (attributes != null && attributes.containsKey("value")) {
            this.setProperty("Value", attributes.get("value"));
        }
        
        // Placeholder text
        if (attributes != null && attributes.containsKey("placeholder")) {
            this.setProperty("PlaceholderText", attributes.get("placeholder"));
        }
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
