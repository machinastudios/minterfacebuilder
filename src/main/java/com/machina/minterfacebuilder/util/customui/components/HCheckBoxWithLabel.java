package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.Alignment;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;

/**
 * H CheckBoxWithLabel component (Group with CheckBox + Label).
 * Based on @CheckBoxWithLabel from Common.ui (line 397-414).
 */
public class HCheckBoxWithLabel extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HCheckBoxWithLabel";

    public HCheckBoxWithLabel() {
        this(null);
    }

    public HCheckBoxWithLabel(Map<String, String> attributes) {
        super("Group");
        
        // Set LayoutMode
        this.setProperty("LayoutMode", "Left");
        
        boolean checked = false;
        if (attributes != null && attributes.containsKey("checked")) {
            checked = Boolean.parseBoolean(attributes.get("checked"));
        }
        
        // Create CheckBox child
        HCheckBox checkBox = new HCheckBox(Map.of("value", String.valueOf(checked)));
        checkBox.setId("CheckBox");
        this.appendChild(checkBox);
        
        // Create Label child
        ComponentBuilder label = new ComponentBuilder("Label");
        String text = "";
        if (attributes != null && attributes.containsKey("text")) {
            text = attributes.get("text");
        }
        label.setProperty("Text", text);
        
        Map<String, Object> labelAnchor = new HashMap<>();
        labelAnchor.put("Right", 30);
        labelAnchor.put("Left", 11);
        label.setProperty("Anchor", labelAnchor);
        
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("FontSize", 16);
        labelStyle.put("TextColor", "#96a9be");
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        label.setProperty("Style", labelStyle);
        
        this.appendChild(label);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
