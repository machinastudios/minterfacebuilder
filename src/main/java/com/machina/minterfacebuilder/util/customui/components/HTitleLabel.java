package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H TitleLabel component with inline expanded styles.
 * Based on @TitleLabel from Common.ui (line 8-10).
 */
public class HTitleLabel extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HTitleLabel";

    public HTitleLabel(Map<String, String> attributes) {
        super("Label");
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        style.put("FontSize", 40);
        style.put("Alignment", "Center");
        this.setProperty("Style", style);
        
        // Text
        if (attributes != null && attributes.containsKey("text")) {
            this.setProperty("Text", attributes.get("text"));
        }
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
