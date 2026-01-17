package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Back Button component (Group container).
 * Based on @BackButton from Common.ui (line 832-837).
 */
public class HBackButton extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HBackButton";

    public HBackButton(Map<String, String> attributes) {
        super("Group");
        
        // Set LayoutMode
        this.setProperty("LayoutMode", "Left");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Left", 50);
        anchor.put("Bottom", 50);
        anchor.put("Width", 110);
        anchor.put("Height", 27);
        this.setProperty("Anchor", anchor);
        
        // Create BackButton child
        ComponentBuilder backButton = new ComponentBuilder("BackButton");
        this.appendChild(backButton);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
