package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Title component with inline expanded styles.
 * Based on @Title from Common.ui (line 594-604).
 */
public class HTitle extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HTitle";

    public HTitle(Map<String, String> attributes) {
        super("Label");
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        style.put("FontSize", 15);
        style.put("VerticalAlignment", "Center");
        style.put("RenderUppercase", true);
        style.put("TextColor", "#b4c8c9");
        style.put("FontName", "Secondary");
        style.put("RenderBold", true);
        style.put("LetterSpacing", 0);
        
        String alignment = "Center";
        if (attributes != null && attributes.containsKey("alignment")) {
            alignment = attributes.get("alignment");
        }
        style.put("HorizontalAlignment", alignment);
        
        this.setProperty("Style", style);
        
        // Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 19);
        this.setProperty("Padding", padding);
        
        // Text
        String text = "";
        if (attributes != null && attributes.containsKey("text")) {
            text = attributes.get("text");
        }
        this.setProperty("Text", text);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
