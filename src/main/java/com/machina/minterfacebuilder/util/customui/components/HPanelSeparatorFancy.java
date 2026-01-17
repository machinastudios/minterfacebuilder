package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H PanelSeparatorFancy component (Group with 3 children).
 * Based on @PanelSeparatorFancy from Common.ui (line 725-745).
 */
public class HPanelSeparatorFancy extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HPanelSeparatorFancy";

    public HPanelSeparatorFancy(Map<String, String> attributes) {
        super("Group");
        
        // Set LayoutMode
        this.setProperty("LayoutMode", "Left");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", 8);
        this.setProperty("Anchor", anchor);
        
        // Create first Group (left line)
        ComponentBuilder leftLine = new ComponentBuilder("Group");
        leftLine.setProperty("FlexWeight", 1);
        leftLine.setProperty("Background", "Common/ContainerPanelSeparatorFancyLine.png");
        this.appendChild(leftLine);
        
        // Create middle Group (decoration)
        ComponentBuilder decoration = new ComponentBuilder("Group");
        Map<String, Object> decAnchor = new HashMap<>();
        decAnchor.put("Width", 11);
        decoration.setProperty("Anchor", decAnchor);
        decoration.setProperty("Background", "Common/ContainerPanelSeparatorFancyDecoration.png");
        this.appendChild(decoration);
        
        // Create third Group (right line)
        ComponentBuilder rightLine = new ComponentBuilder("Group");
        rightLine.setProperty("FlexWeight", 1);
        rightLine.setProperty("Background", "Common/ContainerPanelSeparatorFancyLine.png");
        this.appendChild(rightLine);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
