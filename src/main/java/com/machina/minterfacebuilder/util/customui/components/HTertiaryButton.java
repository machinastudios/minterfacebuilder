package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Tertiary Button component with inline expanded styles.
 * Based on @TertiaryButton from Common.ui (line 299-311).
 */
public class HTertiaryButton extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HTertiaryButton";

    public HTertiaryButton(Map<String, String> attributes) {
        super("Button");
        
        // Configure Anchor (square tertiary button)
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
        defaultState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Tertiary.png\", Border: 12)");
        style.put("Default", defaultState);
        
        // Hovered state - all expanded inline
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Tertiary_Hovered.png\", Border: 12)");
        style.put("Hovered", hoveredState);
        
        // Pressed state - all expanded inline
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Tertiary_Pressed.png\", Border: 12)");
        style.put("Pressed", pressedState);
        
        // Disabled state - all expanded inline
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Disabled.png\", Border: 12)");
        style.put("Disabled", disabledState);
        
        // Sounds using SoundsHelper (no $Sounds)
        style.put("Sounds", SoundsHelper.getButtonsLight());
        
        this.setProperty("Style", style);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
