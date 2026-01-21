package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.Alignment;
import com.machina.minterfacebuilder.helpers.Color;
import com.machina.minterfacebuilder.helpers.FnCall;
import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.components.base.TextButton;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H TextButton component with inline expanded styles.
 * Based on @TextButton from Common.ui (line 167-180).
 */
public class HTextButton extends TextButton {
    public static final String TAG_NAME = "HTextButton";

    public HTextButton() {
        this(null);
    }

    public HTextButton(Map<String, String> attributes) {
        super();
        Map<String, Object> anchor = new HashMap<>();
        int height = 44;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default value
            }
        }
        anchor.put("Height", height);
        this.setProperty("Anchor", anchor);

        Map<String, Object> defaultLabelStyle = new HashMap<>();
        defaultLabelStyle.put("FontSize", 17);
        defaultLabelStyle.put("TextColor", Color.of("#bfcdd5"));
        defaultLabelStyle.put("RenderBold", true);
        defaultLabelStyle.put("RenderUppercase", true);
        defaultLabelStyle.put("HorizontalAlignment", Alignment.CENTER);
        defaultLabelStyle.put("VerticalAlignment", Alignment.CENTER);

        Map<String, Object> disabledLabelStyle = new HashMap<>(defaultLabelStyle);
        disabledLabelStyle.put("TextColor", "#797b7c");

        Map<String, Object> style = new HashMap<>();
        style.put(
            "Default",
            Map.of(
                "Background",
                LiteralValue.of(
                    "PatchStyle(TexturePath: \"Common/Buttons/Primary.png\", VerticalBorder: 12, HorizontalBorder: 80)"
                ),
                "LabelStyle",
                defaultLabelStyle
            )
        );

        style.put(
            "Hovered",
            Map.of(
                "Background",
                LiteralValue.of(
                    "PatchStyle(TexturePath: \"Common/Buttons/Primary_Hovered.png\", VerticalBorder: 12, HorizontalBorder: 80)"
                ),
                "LabelStyle",
                defaultLabelStyle
            )
        );

        style.put(
            "Pressed",
            Map.of(
                "Background",
                LiteralValue.of(
                    "PatchStyle(TexturePath: \"Common/Buttons/Primary_Pressed.png\", VerticalBorder: 12, HorizontalBorder: 80)"
                ),
                "LabelStyle",
                defaultLabelStyle
            )
        );

        style.put(
            "Disabled",
            Map.of(
                "Background",
                LiteralValue.of(
                    "PatchStyle(TexturePath: \"Common/Buttons/Disabled.png\", VerticalBorder: 12, HorizontalBorder: 80)"
                ),
                "LabelStyle",
                disabledLabelStyle
            )
        );

        style.put("Sounds", SoundsHelper.getButtonsLight());

        this.setProperty("Style", FnCall.of("TextButtonStyle", style));
        this.setProperty("Padding", Map.of("Horizontal", 24));

        if (attributes != null && attributes.containsKey("text")) {
            this.setProperty("Text", attributes.get("text"));
        }
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
