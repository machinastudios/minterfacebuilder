package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Subtitle component with inline expanded styles.
 * Based on @Subtitle from Common.ui (line 578-582).
 */
public class HSubtitle extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HSubtitle";

    public HSubtitle(Map<String, String> attributes) {
        super("Label");
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        style.put("FontSize", 15);
        style.put("RenderUppercase", true);
        style.put("TextColor", "#96a9be");
        this.setProperty("Style", style);
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Bottom", 10);
        this.setProperty("Anchor", anchor);
        
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
