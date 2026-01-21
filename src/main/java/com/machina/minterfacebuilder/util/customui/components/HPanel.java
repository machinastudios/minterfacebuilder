package com.machina.minterfacebuilder.util.customui.components;

import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Panel component with inline expanded styles.
 * Based on @Panel from Common.ui (line 4-6).
 */
public class HPanel extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HPanel";

    public HPanel() {
        this(null);
    }

    public HPanel(Map<String, String> attributes) {
        super("Group");
        
        // Configure Background
        this.setProperty("Background", "PatchStyle(TexturePath: \"Common/ContainerFullPatch.png\", Border: 20)");
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
