package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H NumberField component with inline expanded styles.
 * Based on @NumberField from Common.ui (line 434-442).
 */
public class HNumberField extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HNumberField";

    public HNumberField() {
        this(null);
    }

    public HNumberField(Map<String, String> attributes) {
        super("NumberField");
        
        // Configure Style
        Map<String, Object> style = new HashMap<>();
        this.setProperty("Style", style);
        
        // Configure PlaceholderStyle
        Map<String, Object> placeholderStyle = new HashMap<>();
        placeholderStyle.put("TextColor", "#6e7da1");
        this.setProperty("PlaceholderStyle", placeholderStyle);
        
        // Configure Background
        this.setProperty("Background", LiteralValue.of("PatchStyle(TexturePath: \"Common/InputBox.png\", Border: 16)"));
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", 38);
        this.setProperty("Anchor", anchor);
        
        // Configure Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 10);
        this.setProperty("Padding", padding);
        
        // Value
        if (attributes != null && attributes.containsKey("value")) {
            try {
                double value = Double.parseDouble(attributes.get("value"));
                this.setProperty("Value", value);
            } catch (NumberFormatException e) {
                // Invalid number, skip
            }
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
