package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H ColorPicker component with inline expanded styles.
 * Based on @DefaultColorPickerStyle from Common.ui (line 314-325).
 */
public class HColorPicker extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HColorPicker";

    public HColorPicker() {
        this(null);
    }

    public HColorPicker(Map<String, String> attributes) {
        super("ColorPicker");
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        style.put("OpacitySelectorBackground", "Common/ColorPickerOpacitySelectorBackground.png");
        style.put("ButtonBackground", "Common/ColorPickerButton.png");
        style.put("ButtonFill", "Common/ColorPickerFill.png");
        
        Map<String, Object> textFieldDecoration = new HashMap<>();
        Map<String, Object> textFieldDefault = new HashMap<>();
        textFieldDefault.put("Background", "#000000(0.5)");
        textFieldDecoration.put("Default", textFieldDefault);
        style.put("TextFieldDecoration", textFieldDecoration);
        
        Map<String, Object> textFieldPadding = new HashMap<>();
        textFieldPadding.put("Left", 7);
        style.put("TextFieldPadding", textFieldPadding);
        style.put("TextFieldHeight", 32);
        
        this.setProperty("Style", style);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
