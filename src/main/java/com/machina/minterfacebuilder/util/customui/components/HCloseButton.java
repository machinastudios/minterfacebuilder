package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Close Button component with inline expanded styles.
 * Based on @CloseButton from Common.ui (line 338-346).
 */
public class HCloseButton extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HCloseButton";

    public HCloseButton(Map<String, String> attributes) {
        super("Button");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Top", -16);
        anchor.put("Right", -16);
        anchor.put("Width", 32);
        anchor.put("Height", 32);
        this.setProperty("Anchor", anchor);
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        
        // Default state - all expanded inline
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", "Common/ContainerCloseButton.png");
        style.put("Default", defaultState);
        
        // Hovered state - all expanded inline
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", "Common/ContainerCloseButtonHovered.png");
        style.put("Hovered", hoveredState);
        
        // Pressed state - all expanded inline
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", "Common/ContainerCloseButtonPressed.png");
        style.put("Pressed", pressedState);
        
        this.setProperty("Style", style);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
