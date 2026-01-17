package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Cancel TextButton component with inline expanded styles.
 * Based on @CancelTextButton from Common.ui (line 196-209).
 */
public class HCancelTextButton extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HCancelTextButton";

    public HCancelTextButton(Map<String, String> attributes) {
        super("TextButton");
        
        // Configure Anchor
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
        this.setProperty("Anchor", anchor);
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        
        // Default state - all expanded inline
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Destructive.png\", Border: 12)");
        Map<String, Object> defaultLabelStyle = new HashMap<>();
        defaultLabelStyle.put("FontSize", 17);
        defaultLabelStyle.put("TextColor", "#bfcdd5");
        defaultLabelStyle.put("RenderBold", true);
        defaultLabelStyle.put("RenderUppercase", true);
        defaultLabelStyle.put("HorizontalAlignment", "Center");
        defaultLabelStyle.put("VerticalAlignment", "Center");
        defaultState.put("LabelStyle", defaultLabelStyle);
        style.put("Default", defaultState);
        
        // Hovered state - all expanded inline
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Destructive_Hovered.png\", Border: 12)");
        hoveredState.put("LabelStyle", defaultLabelStyle);
        style.put("Hovered", hoveredState);
        
        // Pressed state - all expanded inline
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Destructive_Pressed.png\", Border: 12)");
        pressedState.put("LabelStyle", defaultLabelStyle);
        style.put("Pressed", pressedState);
        
        // Disabled state - all expanded inline
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Disabled.png\", Border: 12)");
        Map<String, Object> disabledLabelStyle = new HashMap<>();
        disabledLabelStyle.put("FontSize", 17);
        disabledLabelStyle.put("TextColor", "#797b7c"); // @DisabledColor expanded
        disabledLabelStyle.put("RenderBold", true);
        disabledLabelStyle.put("RenderUppercase", true);
        disabledLabelStyle.put("HorizontalAlignment", "Center");
        disabledLabelStyle.put("VerticalAlignment", "Center");
        disabledState.put("LabelStyle", disabledLabelStyle);
        style.put("Disabled", disabledState);
        
        // Sounds using SoundsHelper (no $Sounds) - ButtonsCancel
        style.put("Sounds", SoundsHelper.getButtonsCancel());
        
        this.setProperty("Style", style);
        
        // Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 24); // @ButtonPadding expanded
        this.setProperty("Padding", padding);
        
        // Text
        if (attributes != null && attributes.containsKey("text")) {
            this.setProperty("Text", attributes.get("text"));
        }
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
