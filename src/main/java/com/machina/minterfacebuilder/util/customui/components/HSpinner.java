package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Spinner component with inline expanded styles.
 * Based on @DefaultSpinner from Common.ui (line 555-561).
 */
public class HSpinner extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HSpinner";

    public HSpinner() {
        this(null);
    }

    public HSpinner(Map<String, String> attributes) {
        super("Sprite");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 32);
        anchor.put("Height", 32);
        this.setProperty("Anchor", anchor);
        
        // Configure TexturePath
        this.setProperty("TexturePath", "Common/Spinner.png");
        
        // Configure Frame
        Map<String, Object> frame = new HashMap<>();
        frame.put("Width", 32);
        frame.put("Height", 32);
        frame.put("PerRow", 8);
        frame.put("Count", 72);
        this.setProperty("Frame", frame);
        
        // Configure FramesPerSecond
        this.setProperty("FramesPerSecond", 30);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
