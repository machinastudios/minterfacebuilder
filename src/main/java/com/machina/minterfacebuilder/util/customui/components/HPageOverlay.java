package com.machina.minterfacebuilder.util.customui.components;

import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H PageOverlay component with inline expanded styles.
 * Based on @PageOverlay from Common.ui (line 818-820).
 */
public class HPageOverlay extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HPageOverlay";

    public HPageOverlay() {
        this(null);
    }

    public HPageOverlay(Map<String, String> attributes) {
        super("Group");
        
        // Configure Background
        this.setProperty("Background", "#000000(0.45)");
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
