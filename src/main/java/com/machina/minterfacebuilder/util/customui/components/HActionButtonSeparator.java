package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H ActionButtonSeparator component with inline expanded styles.
 * Based on @ActionButtonSeparator from Common.ui (line 568-570).
 */
public class HActionButtonSeparator extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HActionButtonSeparator";

    public HActionButtonSeparator() {
        this(null);
    }

    public HActionButtonSeparator(Map<String, String> attributes) {
        super("Group");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 35);
        this.setProperty("Anchor", anchor);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
