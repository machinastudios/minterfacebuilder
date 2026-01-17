package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H PanelTitle component (Group with Label + separator).
 * Based on @PanelTitle from Common.ui (line 702-718).
 */
public class HPanelTitle extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HPanelTitle";

    public HPanelTitle(Map<String, String> attributes) {
        super("Group");
        
        // Set LayoutMode
        this.setProperty("LayoutMode", "Top");
        
        String alignment = "Start";
        if (attributes != null && attributes.containsKey("alignment")) {
            alignment = attributes.get("alignment");
        }
        
        String text = "";
        if (attributes != null && attributes.containsKey("text")) {
            text = attributes.get("text");
        }
        
        // Create Label #PanelTitle
        ComponentBuilder titleLabel = new ComponentBuilder("Label");
        titleLabel.setId("PanelTitle");
        
        Map<String, Object> titleStyle = new HashMap<>();
        titleStyle.put("RenderBold", true);
        titleStyle.put("VerticalAlignment", "Center");
        titleStyle.put("FontSize", 15);
        titleStyle.put("TextColor", "#afc2c3");
        titleStyle.put("HorizontalAlignment", alignment);
        titleLabel.setProperty("Style", titleStyle);
        
        Map<String, Object> titleAnchor = new HashMap<>();
        titleAnchor.put("Height", 35);
        titleAnchor.put("Horizontal", 8);
        titleLabel.setProperty("Anchor", titleAnchor);
        
        titleLabel.setProperty("Text", text);
        
        this.appendChild(titleLabel);
        
        // Create separator Group
        ComponentBuilder separator = new ComponentBuilder("Group");
        separator.setProperty("Background", "#393426(0.5)");
        
        Map<String, Object> sepAnchor = new HashMap<>();
        sepAnchor.put("Height", 1);
        separator.setProperty("Anchor", sepAnchor);
        
        this.appendChild(separator);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
