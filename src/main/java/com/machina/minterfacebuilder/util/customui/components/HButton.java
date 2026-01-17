package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Button component with inline expanded styles.
 * Based on @Button from Common.ui (line 182-194).
 */
public class HButton extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HButton";

    public HButton(Map<String, String> attributes) {
        super("Button");
        
        // Configure Anchor (square button)
        Map<String, Object> anchor = new HashMap<>();
        int height = 44; // @PrimaryButtonHeight expanded
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        anchor.put("Width", height);
        this.setProperty("Anchor", anchor);
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        
        // Default state - all expanded inline
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Primary_Square.png\", Border: 12)");
        style.put("Default", defaultState);
        
        // Hovered state - all expanded inline
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Primary_Square_Hovered.png\", Border: 12)");
        style.put("Hovered", hoveredState);
        
        // Pressed state - all expanded inline
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Primary_Square_Pressed.png\", Border: 12)");
        style.put("Pressed", pressedState);
        
        // Disabled state - all expanded inline
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Disabled.png\", Border: 12)");
        style.put("Disabled", disabledState);
        
        // Sounds using SoundsHelper (no $Sounds)
        style.put("Sounds", SoundsHelper.getButtonsLight());
        
        this.setProperty("Style", style);
        
        // Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 24); // @ButtonPadding expanded
        this.setProperty("Padding", padding);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
