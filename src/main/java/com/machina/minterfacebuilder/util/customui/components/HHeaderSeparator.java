package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H HeaderSeparator component with inline expanded styles.
 * Based on @HeaderSeparator from Common.ui (line 697-700).
 */
public class HHeaderSeparator extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HHeaderSeparator";

    public HHeaderSeparator() {
        this(null);
    }

    public HHeaderSeparator(Map<String, String> attributes) {
        super("Group");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 5);
        anchor.put("Height", 34);
        this.setProperty("Anchor", anchor);
        
        // Configure Background
        this.setProperty("Background", "Common/HeaderTabSeparator.png");
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
