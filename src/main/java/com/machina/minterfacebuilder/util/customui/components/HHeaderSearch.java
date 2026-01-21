package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H HeaderSearch component (Group with CompactTextField).
 * Based on @HeaderSearch from Common.ui (line 662-682).
 */
public class HHeaderSearch extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HHeaderSearch";

    public HHeaderSearch() {
        this(null);
    }

    public HHeaderSearch(Map<String, String> attributes) {
        super("Group");
        
        // Configure Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 200);
        anchor.put("Right", 0);
        this.setProperty("Anchor", anchor);
        
        // Create CompactTextField child #SearchInput
        ComponentBuilder searchInput = new ComponentBuilder("CompactTextField");
        searchInput.setId("SearchInput");
        
        // SearchInput Anchor
        int marginRight = 10;
        if (attributes != null && attributes.containsKey("marginright")) {
            try {
                marginRight = Integer.parseInt(attributes.get("marginright"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        Map<String, Object> searchAnchor = new HashMap<>();
        searchAnchor.put("Height", 30);
        searchAnchor.put("Right", marginRight);
        searchInput.setProperty("Anchor", searchAnchor);
        
        // SearchInput properties
        searchInput.setProperty("CollapsedWidth", 34);
        searchInput.setProperty("ExpandedWidth", 200);
        
        String placeholderText = "%server.customUI.searchPlaceholder";
        if (attributes != null && attributes.containsKey("placeholder")) {
            placeholderText = attributes.get("placeholder");
        }
        searchInput.setProperty("PlaceholderText", placeholderText);
        
        Map<String, Object> searchStyle = new HashMap<>();
        searchStyle.put("FontSize", 16);
        searchInput.setProperty("Style", searchStyle);
        
        Map<String, Object> placeholderStyle = new HashMap<>();
        placeholderStyle.put("TextColor", "#3d5a85");
        placeholderStyle.put("RenderUppercase", true);
        placeholderStyle.put("FontSize", 14);
        searchInput.setProperty("PlaceholderStyle", placeholderStyle);
        
        Map<String, Object> searchPadding = new HashMap<>();
        searchPadding.put("Horizontal", 12);
        searchPadding.put("Left", 34);
        searchInput.setProperty("Padding", searchPadding);
        
        // Decoration with Icon and ClearButtonStyle
        Map<String, Object> decoration = new HashMap<>();
        Map<String, Object> icon = new HashMap<>();
        icon.put("Texture", "Common/SearchIcon.png");
        icon.put("Width", 16);
        icon.put("Height", 16);
        icon.put("Offset", 9);
        decoration.put("Icon", icon);
        
        Map<String, Object> clearButtonStyle = new HashMap<>();
        Map<String, Object> clearTexture = new HashMap<>();
        clearTexture.put("TexturePath", "Common/ClearInputIcon.png");
        clearTexture.put("Color", "#ffffff(0.3)");
        clearButtonStyle.put("Texture", clearTexture);
        Map<String, Object> clearHoveredTexture = new HashMap<>();
        clearHoveredTexture.put("TexturePath", "Common/ClearInputIcon.png");
        clearHoveredTexture.put("Color", "#ffffff(0.5)");
        clearButtonStyle.put("HoveredTexture", clearHoveredTexture);
        Map<String, Object> clearPressedTexture = new HashMap<>();
        clearPressedTexture.put("TexturePath", "Common/ClearInputIcon.png");
        clearPressedTexture.put("Color", "#ffffff(0.4)");
        clearButtonStyle.put("PressedTexture", clearPressedTexture);
        clearButtonStyle.put("Width", 16);
        clearButtonStyle.put("Height", 16);
        clearButtonStyle.put("Side", "Right");
        clearButtonStyle.put("Offset", 10);
        decoration.put("ClearButtonStyle", clearButtonStyle);
        
        Map<String, Object> defaultDecoration = new HashMap<>();
        defaultDecoration.put("Default", decoration);
        searchInput.setProperty("Decoration", defaultDecoration);
        
        this.appendChild(searchInput);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
