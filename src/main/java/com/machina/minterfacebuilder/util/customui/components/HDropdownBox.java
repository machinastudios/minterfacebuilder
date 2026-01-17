package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H DropdownBox component with inline expanded styles.
 * Based on @DropdownBox from Common.ui (line 479-484).
 */
public class HDropdownBox extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HDropdownBox";

    public HDropdownBox(Map<String, String> attributes) {
        super("DropdownBox");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 330);
        anchor.put("Height", 32); // @DropdownBoxHeight expanded
        this.setProperty("Anchor", anchor);
        
        // Build DropdownBoxStyle with ALL values inline (no aliases)
        Map<String, Object> style = new HashMap<>();
        
        // Backgrounds
        style.put("DefaultBackground", "PatchStyle(TexturePath: \"Common/Dropdown.png\", Border: 16)");
        style.put("HoveredBackground", "PatchStyle(TexturePath: \"Common/DropdownHovered.png\", Border: 16)");
        style.put("PressedBackground", "PatchStyle(TexturePath: \"Common/DropdownPressed.png\", Border: 16)");
        
        // Arrow textures
        style.put("DefaultArrowTexturePath", "Common/DropdownCaret.png");
        style.put("HoveredArrowTexturePath", "Common/DropdownCaret.png");
        style.put("PressedArrowTexturePath", "Common/DropdownPressedCaret.png");
        style.put("ArrowWidth", 13);
        style.put("ArrowHeight", 18);
        
        // Label styles - all expanded inline
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("TextColor", "#96a9be");
        labelStyle.put("RenderUppercase", true);
        labelStyle.put("VerticalAlignment", "Center");
        labelStyle.put("FontSize", 13);
        style.put("LabelStyle", labelStyle);
        
        Map<String, Object> entryLabelStyle = new HashMap<>();
        entryLabelStyle.put("TextColor", "#b7cedd");
        entryLabelStyle.put("RenderUppercase", true);
        entryLabelStyle.put("VerticalAlignment", "Center");
        entryLabelStyle.put("FontSize", 13);
        style.put("EntryLabelStyle", entryLabelStyle);
        
        // Other properties
        style.put("HorizontalPadding", 8);
        style.put("PanelAlign", "Right");
        style.put("PanelOffset", 7);
        style.put("EntryHeight", 31);
        style.put("EntriesInViewport", 10);
        style.put("HorizontalEntryPadding", 7);
        style.put("HoveredEntryBackground", "#0a0f17");
        style.put("PressedEntryBackground", "#0f1621");
        style.put("FocusOutlineSize", 1);
        style.put("FocusOutlineColor", "#ffffff(0.4)");
        
        // Scrollbar style - all expanded inline
        Map<String, Object> scrollbarStyle = new HashMap<>();
        scrollbarStyle.put("Spacing", 6);
        scrollbarStyle.put("Size", 6);
        scrollbarStyle.put("Background", "PatchStyle(TexturePath: \"Common/Scrollbar.png\", Border: 3)");
        scrollbarStyle.put("Handle", "PatchStyle(TexturePath: \"Common/ScrollbarHandle.png\", Border: 3)");
        scrollbarStyle.put("HoveredHandle", "PatchStyle(TexturePath: \"Common/ScrollbarHandleHovered.png\", Border: 3)");
        scrollbarStyle.put("DraggedHandle", "PatchStyle(TexturePath: \"Common/ScrollbarHandleDragged.png\", Border: 3)");
        style.put("PanelScrollbarStyle", scrollbarStyle);
        
        style.put("PanelBackground", "PatchStyle(TexturePath: \"Common/DropdownBox.png\", Border: 16)");
        style.put("PanelPadding", 6);
        
        // Sounds using SoundsHelper (no $Sounds)
        style.put("Sounds", SoundsHelper.getDropdownBox());
        style.put("EntrySounds", SoundsHelper.getButtonsLight());
        
        this.setProperty("Style", style);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
