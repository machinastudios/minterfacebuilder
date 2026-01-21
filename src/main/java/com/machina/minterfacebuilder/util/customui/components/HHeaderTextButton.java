package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.Alignment;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Header TextButton component with inline expanded styles.
 * Based on @HeaderTextButton from Common.ui (line 692-695).
 */
public class HHeaderTextButton extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HHeaderTextButton";

    public HHeaderTextButton(Map<String, String> attributes) {
        super("TextButton");
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        
        // Default LabelStyle - all expanded inline
        Map<String, Object> defaultLabelStyle = new HashMap<>();
        defaultLabelStyle.put("FontSize", 15);
        defaultLabelStyle.put("VerticalAlignment", Alignment.CENTER);
        defaultLabelStyle.put("RenderUppercase", true);
        defaultLabelStyle.put("TextColor", "#d3d6db");
        defaultLabelStyle.put("FontName", "Default");
        defaultLabelStyle.put("RenderBold", true);
        defaultLabelStyle.put("LetterSpacing", 1);
        
        // Default state - all expanded inline
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("LabelStyle", defaultLabelStyle);
        style.put("Default", defaultState);
        
        // Hovered state - all expanded inline
        Map<String, Object> hoveredLabelStyle = new HashMap<>();
        hoveredLabelStyle.put("FontSize", 15);
        hoveredLabelStyle.put("VerticalAlignment", Alignment.CENTER);
        hoveredLabelStyle.put("RenderUppercase", true);
        hoveredLabelStyle.put("TextColor", "#eaebee");
        hoveredLabelStyle.put("FontName", "Default");
        hoveredLabelStyle.put("RenderBold", true);
        hoveredLabelStyle.put("LetterSpacing", 1);
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("LabelStyle", hoveredLabelStyle);
        style.put("Hovered", hoveredState);
        
        // Pressed state - all expanded inline
        Map<String, Object> pressedLabelStyle = new HashMap<>();
        pressedLabelStyle.put("FontSize", 15);
        pressedLabelStyle.put("VerticalAlignment", Alignment.CENTER);
        pressedLabelStyle.put("RenderUppercase", true);
        pressedLabelStyle.put("TextColor", "#b6bbc2");
        pressedLabelStyle.put("FontName", "Default");
        pressedLabelStyle.put("RenderBold", true);
        pressedLabelStyle.put("LetterSpacing", 1);
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("LabelStyle", pressedLabelStyle);
        style.put("Pressed", pressedState);
        
        this.setProperty("Style", style);
        
        // Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Right", 22);
        padding.put("Left", 15);
        padding.put("Bottom", 1);
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
