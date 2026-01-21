package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H ActionButtonContainer component with inline expanded styles.
 * Based on @ActionButtonContainer from Common.ui (line 563-566).
 */
public class HActionButtonContainer extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HActionButtonContainer";

    public HActionButtonContainer() {
        super("Group");
    }

    public HActionButtonContainer(Map<String, String> attributes) {
        super("Group");
        
        // Set LayoutMode
        this.setProperty("LayoutMode", "Right");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Right", 50);
        anchor.put("Bottom", 50);
        anchor.put("Height", 27);
        this.setProperty("Anchor", anchor);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
