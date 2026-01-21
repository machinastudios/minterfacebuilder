package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.Alignment;
import com.machina.minterfacebuilder.helpers.Color;
import com.machina.minterfacebuilder.helpers.FnCall;
import com.machina.minterfacebuilder.util.customui.components.base.TextButton;

/**
 * H Header TextButton component with inline expanded styles.
 * Based on @HeaderTextButton from Common.ui (line 692-695).
 */
public class HHeaderTextButton extends TextButton {
    public static final String TAG_NAME = "HHeaderTextButton";

    public HHeaderTextButton() {
        this(null);
    }

    public HHeaderTextButton(Map<String, String> attributes) {
        super();
        Map<String, Object> style = new HashMap<>();

        Map<String, Object> defaultLabelStyle = new HashMap<>();
        defaultLabelStyle.put("FontSize", 15);
        defaultLabelStyle.put("VerticalAlignment", Alignment.CENTER);
        defaultLabelStyle.put("RenderUppercase", true);
        defaultLabelStyle.put("TextColor", Color.of("#d3d6db"));
        defaultLabelStyle.put("FontName", "Default");
        defaultLabelStyle.put("RenderBold", true);
        defaultLabelStyle.put("LetterSpacing", 1);

        Map<String, Object> hoveredLabelStyle = new HashMap<>(defaultLabelStyle);
        hoveredLabelStyle.put("TextColor", Color.of("#eaebee"));

        Map<String, Object> pressedLabelStyle = new HashMap<>(defaultLabelStyle);
        pressedLabelStyle.put("TextColor", Color.of("#b6bbc2"));

        style.put("Default", Map.of("LabelStyle", defaultLabelStyle));
        style.put("Hovered", Map.of("LabelStyle", hoveredLabelStyle));
        style.put("Pressed", Map.of("LabelStyle", pressedLabelStyle));

        this.setProperty("Style", FnCall.of("TextButtonStyle", style));
        this.setProperty("Padding", Map.of("Right", 22, "Left", 15, "Bottom", 1));

        if (attributes != null && attributes.containsKey("text")) {
            this.setProperty("Text", attributes.get("text"));
        }
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
