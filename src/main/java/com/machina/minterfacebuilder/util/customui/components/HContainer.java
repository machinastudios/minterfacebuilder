package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Container component (Group with Title + Content + CloseButton).
 * Based on @Container from Common.ui (line 750-777).
 */
public class HContainer extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HContainer";

    public HContainer() {
        this(null);
    }

    public HContainer(Map<String, String> attributes) {
        super("Group");
        
        int fullPaddingValue = 9 + 8; // @FullPaddingValue expanded
        boolean showCloseButton = false;
        if (attributes != null && attributes.containsKey("closebutton")) {
            showCloseButton = Boolean.parseBoolean(attributes.get("closebutton"));
        }
        
        // Create Title Group
        ComponentBuilder titleGroup = new ComponentBuilder("Group");
        titleGroup.setId("Title");
        
        Map<String, Object> titleAnchor = new HashMap<>();
        titleAnchor.put("Height", 38); // @TitleHeight expanded
        titleAnchor.put("Top", 0);
        titleGroup.setProperty("Anchor", titleAnchor);
        
        Map<String, Object> titlePadding = new HashMap<>();
        titlePadding.put("Top", 7);
        titleGroup.setProperty("Padding", titlePadding);
        
        titleGroup.setProperty("Background", "PatchStyle(TexturePath: \"Common/ContainerHeaderNoRunes.png\", HorizontalBorder: 35, VerticalBorder: 0)");
        
        this.appendChild(titleGroup);
        
        // Create Content Group
        ComponentBuilder contentGroup = new ComponentBuilder("Group");
        contentGroup.setId("Content");
        
        contentGroup.setProperty("LayoutMode", "Top");
        
        Map<String, Object> contentPadding = new HashMap<>();
        contentPadding.put("Full", fullPaddingValue);
        contentPadding.put("Top", 8);
        contentGroup.setProperty("Padding", contentPadding);
        
        Map<String, Object> contentAnchor = new HashMap<>();
        contentAnchor.put("Top", 38); // @TitleHeight expanded
        contentGroup.setProperty("Anchor", contentAnchor);
        
        contentGroup.setProperty("Background", "PatchStyle(TexturePath: \"Common/ContainerPatch.png\", Border: 23)");
        
        this.appendChild(contentGroup);
        
        // Create CloseButton
        ComponentBuilder closeButton = new ComponentBuilder("Button");
        closeButton.setId("CloseButton");
        
        Map<String, Object> closeAnchor = new HashMap<>();
        closeAnchor.put("Width", 32);
        closeAnchor.put("Height", 32);
        closeAnchor.put("Top", -8);
        closeAnchor.put("Right", -8);
        closeButton.setProperty("Anchor", closeAnchor);
        
        Map<String, Object> closeStyle = new HashMap<>();
        Map<String, Object> closeDefault = new HashMap<>();
        closeDefault.put("Background", "Common/ContainerCloseButton.png");
        closeStyle.put("Default", closeDefault);
        Map<String, Object> closeHovered = new HashMap<>();
        closeHovered.put("Background", "Common/ContainerCloseButtonHovered.png");
        closeStyle.put("Hovered", closeHovered);
        Map<String, Object> closePressed = new HashMap<>();
        closePressed.put("Background", "Common/ContainerCloseButtonPressed.png");
        closeStyle.put("Pressed", closePressed);
        closeStyle.put("Sounds", SoundsHelper.getButtonsCancel());
        closeButton.setProperty("Style", closeStyle);
        
        closeButton.setProperty("Visible", showCloseButton);
        
        this.appendChild(closeButton);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
