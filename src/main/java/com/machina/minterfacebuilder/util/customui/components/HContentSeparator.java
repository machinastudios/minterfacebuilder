package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H ContentSeparator component with inline expanded styles.
 * Based on @ContentSeparator from Common.ui (line 548-553).
 */
public class HContentSeparator extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HContentSeparator";

    public HContentSeparator() {
        this(null);
    }

    public HContentSeparator(Map<String, String> attributes) {
        super("Group");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", 1);
        this.setProperty("Anchor", anchor);
        
        // Configure Background
        this.setProperty("Background", "#2b3542");
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
