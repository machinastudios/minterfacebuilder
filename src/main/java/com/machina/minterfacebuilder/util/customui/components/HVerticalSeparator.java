package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H VerticalSeparator component with inline expanded styles.
 * Based on @VerticalSeparator from Common.ui (line 720-723).
 */
public class HVerticalSeparator extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HVerticalSeparator";

    public HVerticalSeparator() {
        this(null);
    }

    public HVerticalSeparator(Map<String, String> attributes) {
        super("Group");
        
        // Configure Background
        this.setProperty("Background", "PatchStyle(TexturePath: \"Common/ContainerVerticalSeparator.png\")");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 6);
        anchor.put("Top", -2);
        this.setProperty("Anchor", anchor);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
