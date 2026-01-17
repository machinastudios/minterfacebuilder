package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H Scrollbar component with inline expanded styles.
 * Based on @DefaultScrollbarStyle from Common.ui (line 348-355).
 * Note: This is a Style only, typically used as PanelScrollbarStyle in other components.
 */
public class HScrollbar extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HScrollbar";

    public HScrollbar(Map<String, String> attributes) {
        super("Scrollbar");
        
        // Configure Style with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        style.put("Spacing", 6);
        style.put("Size", 6);
        style.put("Background", "PatchStyle(TexturePath: \"Common/Scrollbar.png\", Border: 3)");
        style.put("Handle", "PatchStyle(TexturePath: \"Common/ScrollbarHandle.png\", Border: 3)");
        style.put("HoveredHandle", "PatchStyle(TexturePath: \"Common/ScrollbarHandleHovered.png\", Border: 3)");
        style.put("DraggedHandle", "PatchStyle(TexturePath: \"Common/ScrollbarHandleDragged.png\", Border: 3)");
        
        this.setProperty("Style", style);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
