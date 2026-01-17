package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H CheckBox component with inline expanded styles.
 * Based on @CheckBox from Common.ui (line 390-395).
 */
public class HCheckBox extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HCheckBox";

    public HCheckBox(Map<String, String> attributes) {
        super("CheckBox");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 22);
        anchor.put("Height", 22);
        this.setProperty("Anchor", anchor);
        
        // Configure Background
        this.setProperty("Background", "PatchStyle(TexturePath: \"Common/CheckBoxFrame.png\", Border: 7)");
        
        // Configure Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Full", 4);
        this.setProperty("Padding", padding);
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        
        // Unchecked state - all expanded inline
        Map<String, Object> unchecked = new HashMap<>();
        Map<String, Object> uncheckedDefault = new HashMap<>();
        uncheckedDefault.put("Color", "#00000000");
        unchecked.put("DefaultBackground", uncheckedDefault);
        unchecked.put("HoveredBackground", uncheckedDefault);
        unchecked.put("PressedBackground", uncheckedDefault);
        Map<String, Object> uncheckedDisabled = new HashMap<>();
        uncheckedDisabled.put("Color", "#424242");
        unchecked.put("DisabledBackground", uncheckedDisabled);
        Map<String, Object> untickSound = new HashMap<>();
        untickSound.put("SoundPath", SoundsHelper.getUntick());
        untickSound.put("Volume", 6);
        unchecked.put("ChangedSound", untickSound);
        style.put("Unchecked", unchecked);
        
        // Checked state - all expanded inline
        Map<String, Object> checked = new HashMap<>();
        Map<String, Object> checkedBackground = new HashMap<>();
        checkedBackground.put("TexturePath", "Common/Checkmark.png");
        checked.put("DefaultBackground", checkedBackground);
        checked.put("HoveredBackground", checkedBackground);
        checked.put("PressedBackground", checkedBackground);
        Map<String, Object> tickSound = new HashMap<>();
        tickSound.put("SoundPath", SoundsHelper.getTick());
        tickSound.put("Volume", 6);
        checked.put("ChangedSound", tickSound);
        style.put("Checked", checked);
        
        this.setProperty("Style", style);
        
        // Value
        if (attributes != null && attributes.containsKey("value")) {
            boolean value = Boolean.parseBoolean(attributes.get("value"));
            this.setProperty("Value", value);
        }
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
