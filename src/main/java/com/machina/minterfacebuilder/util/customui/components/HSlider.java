package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Slider component with inline expanded styles.
 * Based on @DefaultSliderStyle from Common.ui (line 822-830).
 */
public class HSlider extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HSlider";

    public HSlider(Map<String, String> attributes) {
        super("Slider");
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        style.put("Background", "PatchStyle(TexturePath: \"Common/SliderBackground.png\", Border: 2)");
        style.put("Handle", "Common/SliderHandle.png");
        style.put("HandleWidth", 16);
        style.put("HandleHeight", 16);
        
        Map<String, Object> sounds = new HashMap<>();
        Map<String, Object> hoverSound = new HashMap<>();
        hoverSound.put("SoundPath", SoundsHelper.getButtonsLightHover());
        hoverSound.put("Volume", 6);
        sounds.put("MouseHover", hoverSound);
        style.put("Sounds", sounds);
        
        this.setProperty("Style", style);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
